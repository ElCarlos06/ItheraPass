const pool = require('../helpers/mysql-config');

// --- FUNCIONES DE TURNOS ---

const createTurno = async (req, res) => {
    const { idFila, idUsuario, nombreUsuario, correoUsuario } = req.body;
    
    const connection = await pool.getConnection();
    
    try {
        await connection.beginTransaction();
        
        // 1. INSERTAR O ACTUALIZAR USUARIO (equivalente a MERGE en Oracle)
        await connection.query(
            `INSERT INTO Usuario (id, nombre, correo) 
             VALUES (?, ?, ?)
             ON DUPLICATE KEY UPDATE nombre = ?, correo = ?`,
            [idUsuario, nombreUsuario, correoUsuario || "", nombreUsuario, correoUsuario || ""]
        );

        // 2. VALIDAR QUE LA FILA EXISTA
        const idFilaInt = parseInt(idFila);
        const [filaRows] = await connection.query("SELECT id FROM Fila WHERE id = ?", [idFilaInt]);
        
        if (filaRows.length === 0) {
            await connection.rollback();
            return res.status(404).json({ success: false, message: "Fila no existe" });
        }

        // 3. VALIDACIÓN DUPLICADOS
        const [existingTurno] = await connection.query(
            "SELECT id FROM Turno WHERE idFila = ? AND idUsuario = ? AND estado = 'EN_ESPERA'", 
            [idFilaInt, idUsuario]
        );
        
        if (existingTurno.length > 0) {
            await connection.rollback();
            return res.json({ success: false, message: "Ya estás formado en esta fila" });
        }

        // 4. ACTUALIZAR CONTADOR DE LA FILA
        await connection.query(
            "UPDATE Fila SET cantidadEnEspera = cantidadEnEspera + 1 WHERE id = ?", 
            [idFilaInt]
        );

        // 5. INSERTAR TURNO
        const codigoTemp = "T-WAIT";
        const [insertResult] = await connection.query(
            `INSERT INTO Turno (idFila, idUsuario, nombreUsuario, correoUsuario, estado, codigoTurno) 
             VALUES (?, ?, ?, ?, 'EN_ESPERA', ?)`,
            [idFilaInt, idUsuario, nombreUsuario, correoUsuario || "", codigoTemp]
        );
        
        const nuevoId = insertResult.insertId;
        const codigoFinal = "A-" + nuevoId;
        
        // 6. ACTUALIZAR CÓDIGO DEL TURNO
        await connection.query(
            "UPDATE Turno SET codigoTurno = ? WHERE id = ?", 
            [codigoFinal, nuevoId]
        );

        await connection.commit();
        res.json({ success: true, id: nuevoId });

    } catch (error) {
        await connection.rollback();
        console.error('Error en createTurno:', error.message);
        res.status(500).json({ success: false, message: "Error al formar: " + error.message });
    } finally {
        connection.release();
    }
};

// OBTENER TURNOS DE UNA FILA
const getTurnos = async (req, res) => {
    const idFila = req.params.id;
    try {
        const [rows] = await pool.query(
            `SELECT id, codigoTurno, idFila, idUsuario, 
             nombreUsuario, correoUsuario, estado, 
             DATE_FORMAT(fecha, '%Y-%m-%d %H:%i:%s') as fecha 
             FROM Turno WHERE idFila = ?`,
            [idFila]
        );
        res.json(rows);
    } catch (error) {
        console.error('Error en getTurnos:', error.message);
        res.status(500).json({ message: "Error al obtener turnos: " + error.message });
    }
};

// ACTUALIZAR ESTADO (LLAMAR/ATENDER)
const updateTurno = async (req, res) => {
    const idTurno = req.params.id;
    const { estado } = req.body;
    
    const connection = await pool.getConnection();

    try {
        await connection.beginTransaction();
        
        // Obtener info del turno para saber la fila
        const [turnoRows] = await connection.query(
            `SELECT idFila FROM Turno WHERE id = ?`, 
            [idTurno]
        );
        
        if (turnoRows.length === 0) {
            await connection.rollback();
            return res.status(404).json({ success: false, message: "Turno no encontrado" });
        }
        
        const turno = turnoRows[0];

        // Actualizar estado
        await connection.query(
            "UPDATE Turno SET estado = ? WHERE id = ?", 
            [estado, idTurno]
        );

        if (estado === "FINALIZADO") {
            await connection.query(
                "UPDATE Fila SET cantidadEnEspera = cantidadEnEspera - 1, cantidadAtendidos = cantidadAtendidos + 1 WHERE id = ?", 
                [turno.idFila]
            );
        }

        await connection.commit();
        res.json({ success: true });
    } catch (error) {
        await connection.rollback();
        console.error('Error en updateTurno:', error.message);
        res.status(500).json({ success: false, message: "Error: " + error.message });
    } finally {
        connection.release();
    }
};

// MIS FILAS ACTIVAS
const getMisFilas = async (req, res) => {
    const idUsuario = req.params.idUsuario;
    try {
        const [rows] = await pool.query(
            `SELECT f.id, f.nombre, f.categoria, f.estado,
                   f.tiempoPromedioAtencion, f.cantidadEnEspera,
                   t.codigoTurno, t.estado as estadoTurno, t.id as idTurno
            FROM Turno t
            JOIN Fila f ON t.idFila = f.id
            WHERE t.idUsuario = ? 
            AND t.estado NOT IN ('FINALIZADO', 'CANCELADO')`,
            [idUsuario]
        );
        res.json(rows);
    } catch (error) {
        console.error('Error en getMisFilas:', error.message);
        res.status(500).json({ message: "Error al cargar mis filas: " + error.message });
    }
};

module.exports = {
    createTurno,
    getTurnos,
    updateTurno,
    getMisFilas
};
