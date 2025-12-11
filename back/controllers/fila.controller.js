const oracledb = require('oracledb');
// Al requerir el config, se inicializa el Pool de conexiones automáticamente
require('../helpers/oracle-config'); 

const outFormatObj = { outFormat: oracledb.OUT_FORMAT_OBJECT };

// --- FUNCIONES DE FILAS ---

const getFilas = async (req, res) => {
    let connection;
    try {
        connection = await oracledb.getConnection();
        // Usamos alias "nombre" para forzar minúsculas y que Android lo entienda
        const sql = `SELECT id "id", nombre "nombre", categoria "categoria", estado "estado", 
                     tiempoPromedioAtencion "tiempoPromedioAtencion", cantidadEnEspera "cantidadEnEspera", 
                     cantidadAtendidos "cantidadAtendidos", idPropietario "idPropietario" 
                     FROM Fila`;
        const result = await connection.execute(sql, [], outFormatObj);
        res.json(result.rows);
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Error al obtener filas" });
    } finally {
        if (connection) await connection.close();
    }
};

const getFila = async (req, res) => {
    const id = req.params.id;
    let connection;
    try {
        connection = await oracledb.getConnection();
        const sql = `SELECT id "id", nombre "nombre", categoria "categoria", estado "estado", 
                     tiempoPromedioAtencion "tiempoPromedioAtencion" 
                     FROM Fila WHERE id = :1`;
        const result = await connection.execute(sql, [id], outFormatObj);
        
        if (result.rows.length > 0) res.json(result.rows[0]);
        else res.status(404).json({ message: "No encontrada" });
    } catch (error) {
        console.error(error);
        res.status(500).json({ message: "Error del servidor" });
    } finally {
        if (connection) await connection.close();
    }
};

const createFila = async (req, res) => {
    const { nombre, categoria, tiempoPromedioAtencion, idPropietario, estado } = req.body;
    let connection;
    try {
        connection = await oracledb.getConnection();
        
        // Oracle necesita RETURNING INTO para devolver el ID generado
        // Y los parámetros son :1, :2...
        const sql = `INSERT INTO Fila (nombre, categoria, tiempoPromedioAtencion, idPropietario, estado) 
                     VALUES (:1, :2, :3, :4, :5) 
                     RETURNING id INTO :6`;
                     
        const result = await connection.execute(sql, [
            nombre, 
            categoria, 
            tiempoPromedioAtencion || 5, 
            idPropietario, 
            estado || 'ABIERTA',
            { type: oracledb.NUMBER, dir: oracledb.BIND_OUT } // Variable de salida para el ID
        ], { autoCommit: true }); // autoCommit guarda los cambios
        
        // El ID recuperado está en result.outBinds[0][0]
        const newId = result.outBinds[0][0];
        
        res.json({ success: true, id: newId, message: "Fila creada" });
    } catch (error) {
        console.error(error);
        res.status(500).json({ success: false, message: "Error al crear fila" });
    } finally {
        if (connection) await connection.close();
    }
};

const updateFila = async (req, res) => {
    res.json({ message: "No se actualizan las filas :D" });
};

const deleteFila = async (req, res) => {
    const id = req.params.id;
    let connection;
    try {
        connection = await oracledb.getConnection();
        const sql = "DELETE FROM Fila WHERE id = :1";
        await connection.execute(sql, [id], { autoCommit: true });
        res.json({ success: true });
    } catch (error) {
        console.error(error);
        res.status(500).json({ success: false, message: "Error al borrar" });
    } finally {
        if (connection) await connection.close();
    }
};

const createTurno = async (req, res) => {
    const { idFila, idUsuario, nombreUsuario } = req.body;
    
    console.log(`Usuario ${nombreUsuario} se formó en fila ${idFila}`);

    const idFilaInt = parseInt(idFila);
    const fila = filas.find(f => f.id === idFilaInt); 

    if (fila) {
        fila.cantidadEnEspera++;
        
        const nuevoTurno = {
            id: contadorTurnos++,
            idFila: idFilaInt,
            idUsuario: idUsuario,
            nombreUsuario: nombreUsuario,
            fecha: new Date(),
            estado: "EN_ESPERA"
        };
        turnos.push(nuevoTurno);

        res.json({ 
            success: true, 
            affectedRows: 1, 
            turno: nuevoTurno 
        });
    } else {
        res.status(404).json({ success: false, message: "Fila no encontrada" });
    }
};

const getTurnos = (req, res) => {
    const idFila = parseInt(req.params.id);
    // Filtra los turnos que coincidan con el ID de la fila
    const turnosDeFila = turnos.filter(t => t.idFila === idFila);
    res.json(turnosDeFila);
};

const updateTurno = (req, res) => {
    const idTurno = parseInt(req.params.id);
    const { estado } = req.body;
    
    const turno = turnos.find(t => t.id === idTurno);
    if (turno) {
        turno.estado = estado; // Actualizamos el estado
        // Lógica extra: Si finalizamos a alguien, actualizamos los contadores de la fila
        if (estado === "FINALIZADO") {
            const fila = filas.find(f => f.id === turno.idFila);
            if (fila) {
                if(fila.cantidadEnEspera > 0) fila.cantidadEnEspera--;
                fila.cantidadAtendidos++;
            }
        }
        res.json({ success: true });
    } else {
        res.status(404).json({ success: false, message: "Turno no encontrado" });
    }
};

module.exports = {
    getFilas,
    getFila,
    createFila,
    updateFila,
    deleteFila
};