const oracledb = require('oracledb');
require('dotenv').config();

oracledb.queueTimeout = 240000;

// Configuración de la conexión
const config = {
    user: process.env.DB_USER || 'ADMIN',
    password: process.env.DB_PASSWORD || 'QRIthera2025',
    
    connectString: process.env.DB_CONNECT_STRING || 'vogth7pwvzhvz45d_high',
    
    
    configDir: './wallet',
    walletLocation: './wallet',
    walletPassword: process.env.WALLET_PASSWORD 
};

async function initialize() {
    try {
        await oracledb.createPool(config);
        console.log('Conexión Funcionando');
    } catch (err) {
        console.error('Error al conectar', err);
        process.exit(1);
    }
}

// Función helper para ejecutar SQL
async function query(sql, params = [], options = {}) {
    let connection;
    try {
        connection = await oracledb.getConnection();
        
        const result = await connection.execute(sql, params, { autoCommit: true, ...options });
        return result;
    } catch (err) {
        console.error('Error en query:', err);
        throw err;
    } finally {
        if (connection) {
            try {
                await connection.close(); // Liberar conexión al pool
            } catch (err) {
                console.error(err);
            }
        }
    }
}

// Inicializamos el pool al cargar el archivo
initialize();

module.exports = { query };