const pool = require('../helpers/mysql-config');

// --- FUNCIONES DE FILAS ---

const getFilas = async (req, res) => {
    try {
        const [rows] = await pool.query(
            `SELECT id, nombre, categoria, estado, 
             tiempoPromedioAtencion, cantidadEnEspera, 
             cantidadAtendidos, idPropietario 
             FROM Fila`
        );
        res.json(rows);
    } catch (error) {
        console.error('Error en getFilas:', error.message);
        res.status(500).json({ 
            message: "Error al obtener filas: " + error.message
        });
    }
};

const getFila = async (req, res) => {
    const id = req.params.id;
    try {
        const [rows] = await pool.query(
            `SELECT id, nombre, categoria, estado, 
             tiempoPromedioAtencion 
             FROM Fila WHERE id = ?`,
            [id]
        );
        
        if (rows.length > 0) res.json(rows[0]);
        else res.status(404).json({ message: "No encontrada" });
    } catch (error) {
        console.error('Error en getFila:', error.message);
        res.status(500).json({ message: "Error del servidor" });
    }
};

const createFila = async (req, res) => {
    const { nombre, categoria, tiempoPromedioAtencion, idPropietario, estado } = req.body;
    try {
        const [result] = await pool.query(
            `INSERT INTO Fila (nombre, categoria, tiempoPromedioAtencion, idPropietario, estado) 
             VALUES (?, ?, ?, ?, ?)`,
            [
                nombre, 
                categoria, 
                tiempoPromedioAtencion || 5, 
                idPropietario, 
                estado || 'ABIERTA'
            ]
        );
        
        const newId = result.insertId;
        res.json({ success: true, id: newId, message: "Fila creada" });
    } catch (error) {
        console.error('Error en createFila:', error.message);
        res.status(500).json({ success: false, message: "Error al crear fila: " + error.message });
    }
};

const updateFila = async (req, res) => {
    res.json({ message: "No se actualizan las filas :D" });
};

const deleteFila = async (req, res) => {
    const id = req.params.id;
    try {
        const [result] = await pool.query("DELETE FROM Fila WHERE id = ?", [id]);
        
        if (result.affectedRows > 0) {
            res.json({ success: true, affectedRows: result.affectedRows });
        } else {
            res.status(404).json({ success: false, message: "Fila no encontrada" });
        }
    } catch (error) {
        console.error('Error en deleteFila:', error.message);
        res.status(500).json({ success: false, message: "Error al borrar: " + error.message });
    }
};

module.exports = {
    getFilas,
    getFila,
    createFila,
    updateFila,
    deleteFila
};
