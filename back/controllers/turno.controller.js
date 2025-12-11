const oracledb = require('oracledb');
// Al requerir el config, se inicializa el Pool de conexiones automáticamente
require('../helpers/oracle-config'); 

// --- FUNCIONES DE TURNOS ---

const createTurno = async (req, res) => {
    const { idFila, idUsuario, nombreUsuario, correoUsuario } = req.body;
    let connection;
    
    try {
        connection = await oracledb.getConnection();
        
        const mergeUserSql = `
            MERGE INTO Usuario u 
            USING (SELECT :1 as id, :2 as nombre, :3 as correo FROM dual) s
            ON (u.id = s.id)
            WHEN MATCHED THEN UPDATE SET u.nombre = s.nombre, u.correo = s.correo
            WHEN NOT MATCHED THEN INSERT (id, nombre, correo) VALUES (s.id, s.nombre, s.correo)
        `;
        await connection.execute(mergeUserSql, [idUsuario, nombreUsuario, correoUsuario || ""]);

        
        const idFilaInt = parseInt(idFila);
        const filaResult = await connection.execute("SELECT id FROM Fila WHERE id = :1", [idFilaInt]);
        
        if (filaResult.rows.length === 0) {
            return res.status(404).json({ success: false, message: "Fila no existe" });
        }

        // 3. VALIDACIÓN DUPLICADOS
        const existingTurno = await connection.execute(
            "SELECT id FROM Turno WHERE idFila = :1 AND idUsuario = :2 AND estado = 'EN_ESPERA'", 
            [idFilaInt, idUsuario]
        );
        
        if (existingTurno.rows && existingTurno.rows.length > 0) {
            return res.json({ success: false, message: "Ya estás formado en esta fila" });
        }

        
        await connection.execute("UPDATE Fila SET cantidadEnEspera = cantidadEnEspera + 1 WHERE id = :1", [idFilaInt]);

        const codigoTemp = "T-WAIT"; 
        
        
        const insertSql = `
            INSERT INTO Turno (idFila, idUsuario, nombreUsuario, correoUsuario, estado, codigoTurno) 
            VALUES (:1, :2, :3, :4, 'EN_ESPERA', :5)
            RETURNING id INTO :6
        `;
        
        const insertResult = await connection.execute(insertSql, [
            idFilaInt, 
            idUsuario, 
            nombreUsuario, 
            correoUsuario || "", 
            codigoTemp,
            { type: oracledb.NUMBER, dir: oracledb.BIND_OUT }
        ]);
        
        const nuevoId = insertResult.outBinds[0][0];
        const codigoFinal = "A-" + nuevoId;
        
       
        await connection.execute("UPDATE Turno SET codigoTurno = :1 WHERE id = :2", [codigoFinal, nuevoId]);

        await connection.commit();
        
        res.json({ success: true, id: nuevoId });

    } catch (error) {
        if (connection) await connection.rollback();
        console.error(error);
        res.status(500).json({ success: false, message: "Error al formar: " + error.message });
    } finally {
        if (connection) await connection.close();
    }
};

// OBTENER TURNOS DE UNA FILA
const getTurnos = async (req, res) => {
    const idFila = req.params.id;
    let connection;
    try {
        connection = await oracledb.getConnection();
        // Usamos alias para minúsculas
        const sql = `SELECT id "id", codigoTurno "codigoTurno", idFila "idFila", idUsuario "idUsuario", 
                     nombreUsuario "nombreUsuario", correoUsuario "correoUsuario", estado "estado", 
                     TO_CHAR(fecha, 'YYYY-MM-DD HH24:MI:SS') "fecha" 
                     FROM Turno WHERE idFila = :1`;
        const result = await connection.execute(sql, [idFila], outFormatObj);
        res.json(result.rows);
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Error al obtener turnos" });
    } finally {
        if (connection) await connection.close();
    }
};

// ACTUALIZAR ESTADO (LLAMAR/ATENDER)
const updateTurno = async (req, res) => {
    const idTurno = req.params.id;
    const { estado } = req.body;
    let connection;

    try {
        connection = await oracledb.getConnection();
        
        // Obtener info del turno para saber la fila
        const turnoResult = await connection.execute(`SELECT idFila "idFila" FROM Turno WHERE id = :1`, [idTurno], outFormatObj);
        if (turnoResult.rows.length === 0) return res.status(404).json({ success: false });
        
        const turno = turnoResult.rows[0];

        // Actualizar estado
        await connection.execute("UPDATE Turno SET estado = :1 WHERE id = :2", [estado, idTurno]);

        if (estado === "FINALIZADO") {
            const updateSql = "UPDATE Fila SET cantidadEnEspera = cantidadEnEspera - 1, cantidadAtendidos = cantidadAtendidos + 1 WHERE id = :1";
            await connection.execute(updateSql, [turno.idFila]);
        }

        await connection.commit();
        res.json({ success: true });
    } catch (error) {
        if (connection) await connection.rollback();
        console.error(error);
        res.status(500).json({ success: false });
    } finally {
        if (connection) await connection.close();
    }
};

// MIS FILAS ACTIVAS
const getMisFilas = async (req, res) => {
    const idUsuario = req.params.idUsuario;
    let connection;
    try {
        connection = await oracledb.getConnection();
        // Alias con comillas dobles para que el JSON salga {"id": 1} y no {"ID": 1}
        const sql = `
            SELECT f.id "id", f.nombre "nombre", f.categoria "categoria", f.estado "estado",
                   f.tiempoPromedioAtencion "tiempoPromedioAtencion", f.cantidadEnEspera "cantidadEnEspera",
                   t.codigoTurno "codigoTurno", t.estado "estadoTurno", t.id "idTurno"
            FROM Turno t
            JOIN Fila f ON t.idFila = f.id
            WHERE t.idUsuario = :1 
            AND t.estado NOT IN ('FINALIZADO', 'CANCELADO')
        `;
        const result = await connection.execute(sql, [idUsuario], outFormatObj);
        res.json(result.rows);
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Error al cargar mis filas" });
    } finally {
        if (connection) await connection.close();
    }
};

module.exports = {
    createTurno,
    getTurnos,
    updateTurno,
    getMisFilas
};