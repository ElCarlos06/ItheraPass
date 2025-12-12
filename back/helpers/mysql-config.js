const mysql = require('mysql2/promise');
require('dotenv').config();

// Configuración de MySQL
const config = {
    host: process.env.DB_HOST || 'localhost',
    user: process.env.DB_USER || 'root',
    password: process.env.DB_PASSWORD || '',
    database: process.env.DB_NAME || 'itherapass',
    port: process.env.DB_PORT || 3306,
    waitForConnections: true,
    connectionLimit: 10,
    queueLimit: 0,
    enableKeepAlive: true,
    keepAliveInitialDelay: 0
};

const pool = mysql.createPool(config);

// Verificar conexión al iniciar
pool.getConnection()
    .then(connection => {
        console.log('Conexión a MySQL establecida correctamente');
        console.log(`Base de datos: ${config.database}`);
        connection.release();
    })
    .catch(err => {
        console.error('Error al conectar a MySQL:', err.message);
        console.error('Verifica que MySQL esté corriendo y que la base de datos exista');
    });

module.exports = pool;