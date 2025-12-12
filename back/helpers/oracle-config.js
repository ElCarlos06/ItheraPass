const oracledb = require('oracledb');
require('dotenv').config();

// Timeout global para la cola de conexiones (4 minutos)
oracledb.queueTimeout = 240000;

// Configuración de la conexión
const config = {
    user: process.env.DB_USER || 'ADMIN',
    password: process.env.DB_PASSWORD || 'QRIthera2025',
    
    connectString: process.env.DB_CONNECT_STRING || 'vogth7pwvzhvz45d_high',
    
    // IMPORTANTE: Para Oracle Cloud Autonomous Database, el wallet debe estar configurado así:
    configDir: './wallet',
    walletLocation: './wallet',
    // La contraseña del wallet es la que configuraste cuando descargaste el wallet
    // Si no la configuraste, déjala vacía o usa la que te dieron al crear la BD
    walletPassword: process.env.WALLET_PASSWORD || '',
    
    // Configuración del pool más conservadora para evitar saturación
    poolMin: 1,           // Mínimo de conexiones (empezar con 1)
    poolMax: 5,           // Máximo de conexiones (reducido para evitar saturación)
    poolIncrement: 1,     // Incremento de conexiones
    poolTimeout: 60,       // Tiempo máximo para obtener conexión del pool
    queueTimeout: 120000,  // 2 minutos para esperar en la cola (reducido)
    stmtCacheSize: 30      // Cache de statements
};

let poolInitialized = false;

async function initialize() {
    if (poolInitialized) {
        return; // Ya está inicializado
    }
    
    try {
        console.log('Intentando conectar a Oracle...');
        console.log('Connect String:', config.connectString);
        console.log('User:', config.user);
        console.log('Wallet Location:', config.walletLocation);
        
        await oracledb.createPool(config);
        poolInitialized = true;
        console.log('✅ Conexión a Oracle establecida correctamente');
    } catch (err) {
        console.error('❌ Error al conectar a Oracle:', err.message);
        console.error('Código de error:', err.code);
        
        // No salir del proceso, permitir que el servidor siga corriendo
        // Los endpoints devolverán error 500 si Oracle no está disponible
        console.warn('⚠️  El servidor continuará sin conexión a Oracle');
        console.warn('⚠️  Los endpoints que requieren BD devolverán error 500');
    }
}

// Función helper para ejecutar SQL
async function query(sql, params = [], options = {}) {
    if (!poolInitialized) {
        throw new Error('Pool de Oracle no inicializado. Verifica la conexión a la base de datos.');
    }
    
    let connection;
    try {
        connection = await oracledb.getConnection();
        
        const result = await connection.execute(sql, params, { autoCommit: true, ...options });
        return result;
    } catch (err) {
        console.error('Error en query:', err.message);
        console.error('Código:', err.code);
        throw err;
    } finally {
        if (connection) {
            try {
                await connection.close(); // Liberar conexión al pool
            } catch (err) {
                console.error('Error al cerrar conexión:', err.message);
            }
        }
    }
}

// Inicializamos el pool al cargar el archivo
initialize();

module.exports = { query };