const pool = require('../helpers/mysql-config');

// turnos

/**
 * Limpia turnos en estado ATENDIENDO que tienen más de 5 minutos sin actualizarse
 * Esto previene que turnos "huérfanos" impidan el reinicio del contador
 */
const limpiarTurnosAtendiendoAntiguos = async (connection, idFila) => {
    try {
        // Buscar turnos en ATENDIENDO con más de 5 minutos de antigüedad
        const [turnosAntiguos] = await connection.query(
            `SELECT id FROM Turno 
             WHERE idFila = ? 
             AND estado = 'ATENDIENDO' 
             AND TIMESTAMPDIFF(MINUTE, updated_at, NOW()) > 5`,
            [idFila]
        );
        
        if (turnosAntiguos.length > 0) {
            const ids = turnosAntiguos.map(t => t.id);
            
            // Cambiar a FINALIZADO para que no bloqueen el contador
            await connection.query(
                `UPDATE Turno SET estado = 'ATENDIDO' WHERE id IN (${ids.map(() => '?').join(',')})`,
                ids
            );
            
            // Actualizar contadores de la fila
            await connection.query(
                `UPDATE Fila SET cantidadAtendidos = cantidadAtendidos + ? WHERE id = ?`,
                [turnosAntiguos.length, idFila]
            );
        }
    } catch (error) {
        console.error('[limpiarTurnosAtendiendoAntiguos] Error:', error.message);
        // No lanzar error, solo loguear para no interrumpir el flujo principal
    }
};

const createTurno = async (req, res) => {
    const { idFila, idUsuario, nombreUsuario, correoUsuario } = req.body;
    
    const connection = await pool.getConnection();
    
    try {
        await connection.beginTransaction();
        
        // 1. insertar o actualizar xd
        await connection.query(
            `INSERT INTO Usuario (id, nombre, correo) 
             VALUES (?, ?, ?)
             ON DUPLICATE KEY UPDATE nombre = ?, correo = ?`,
            [idUsuario, nombreUsuario, correoUsuario || "", nombreUsuario, correoUsuario || ""]
        );

        // 2. hay fila?
        const idFilaInt = parseInt(idFila);
        const [filaRows] = await connection.query("SELECT id FROM Fila WHERE id = ?", [idFilaInt]);
        
        if (filaRows.length === 0) {
            await connection.rollback();
            return res.status(404).json({ success: false, message: "Fila no existe" });
        }

        // 3. Verificar si ya está formado en esta fila (cualquier estado activo)
        const [existingTurno] = await connection.query(
            "SELECT id, estado FROM Turno WHERE idFila = ? AND idUsuario = ? AND estado IN ('EN_ESPERA', 'ATENDIENDO')", 
            [idFilaInt, idUsuario]
        );
        
        if (existingTurno.length > 0) {
            await connection.rollback();
            return res.json({ success: false, message: "Ya estás formado en esta fila" });
        }

        // 4. Limpiar turnos ATENDIENDO antiguos antes de contar
        await limpiarTurnosAtendiendoAntiguos(connection, idFilaInt);

        // 5. Contar SOLO turnos realmente activos de ESTA FILA ESPECÍFICA (EN_ESPERA o ATENDIENDO)
        // IMPORTANTE: Contar ANTES de insertar el nuevo turno para obtener la posición correcta
        // Usamos la misma conexión para que vea los cambios dentro de la transacción
        const [countRows] = await connection.query(
            `SELECT COUNT(*) as total FROM Turno 
             WHERE idFila = ? AND estado IN ('EN_ESPERA', 'ATENDIENDO')`,
            [idFilaInt]
        );
        
        const totalActivos = parseInt(countRows[0].total) || 0;
        const posicion = totalActivos + 1;
        
        // 6. Actualizar contador de la fila
        await connection.query(
            "UPDATE Fila SET cantidadEnEspera = cantidadEnEspera + 1 WHERE id = ?", 
            [idFilaInt]
        );

        // 7. Insertar turno con código basado en posición
        const codigoFinal = posicion.toString();
        const [insertResult] = await connection.query(
            `INSERT INTO Turno (idFila, idUsuario, nombreUsuario, correoUsuario, estado, codigoTurno) 
             VALUES (?, ?, ?, ?, 'EN_ESPERA', ?)`,
            [idFilaInt, idUsuario, nombreUsuario, correoUsuario || "", codigoFinal]
        );
        
        const nuevoId = insertResult.insertId;

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

// Obtener turnos de la fila (solo activos, sin finalizados)
const getTurnos = async (req, res) => {
    const idFila = req.params.id;
    try {
        // Solo devolver turnos activos (EN_ESPERA o ATENDIENDO), NO finalizados
        const [rows] = await pool.query(
            `SELECT id, codigoTurno, idFila, idUsuario, 
             nombreUsuario, correoUsuario, estado, 
             DATE_FORMAT(fecha, '%Y-%m-%d %H:%i:%s') as fecha 
             FROM Turno 
             WHERE idFila = ? AND estado IN ('EN_ESPERA', 'ATENDIENDO')
             ORDER BY id ASC`,
            [idFila]
        );
        res.json(rows);
    } catch (error) {
        console.error('Error en getTurnos:', error.message);
        res.status(500).json({ message: "Error al obtener turnos: " + error.message });
    }
};

// estado de atender
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

        if (estado === "ATENDIDO") {
            await connection.query(
                "UPDATE Fila SET cantidadEnEspera = cantidadEnEspera - 1, cantidadAtendidos = cantidadAtendidos + 1 WHERE id = ?", 
                [turno.idFila]
            );
        } else if (estado === "ATENDIENDO") {
            // Cuando se marca como ATENDIENDO, actualizar cantidadEnEspera
            await connection.query(
                "UPDATE Fila SET cantidadEnEspera = cantidadEnEspera - 1 WHERE id = ?", 
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

// filas activas
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
            AND t.estado NOT IN ('ATENDIDO', 'CANCELADO')`,
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
