const mysql = require('mysql2/promise')

const config ={
    host: 'adb.us-phoenix-1.oraclecloud.com',
    user: 'ADMIN',
    password: 'QRIthera2025',
    database: 'dreamhome',
    port: 1522,
    connectionLimit: 10
}

const pool = mysql.createPool(config)

module.exports = pool