/**
 * Script para truncar (vaciar) todas las tablas de la base de datos
 * ⚠️ ADVERTENCIA: Esto eliminará TODOS los datos de las tablas
 * 
 * Uso: node truncate_db.js
 */

const pool = require('./helpers/mysql-config');
const readline = require('readline');

const rl = readline.createInterface({
    input: process.stdin,
    output: process.stdout
});

async function truncateDatabase() {
    const connection = await pool.getConnection();
    
    try {
        console.log('⚠️  ADVERTENCIA: Esto eliminará TODOS los datos de las tablas');
        console.log('Tablas que se truncarán: Turno, Fila, Usuario\n');
        
        // Confirmación
        const answer = await new Promise((resolve) => {
            rl.question('¿Estás seguro de que quieres continuar? (escribe "SI" para confirmar): ', resolve);
        });
        
        if (answer.toUpperCase() !== 'SI') {
            console.log('❌ Operación cancelada');
            rl.close();
            return;
        }
        
        await connection.beginTransaction();
        
        // Desactivar verificación de foreign keys temporalmente
        await connection.query('SET FOREIGN_KEY_CHECKS = 0');
        
        // Truncar tablas
        console.log('\n🗑️  Truncando tablas...');
        await connection.query('TRUNCATE TABLE Turno');
        console.log('   ✅ Turno truncada');
        
        await connection.query('TRUNCATE TABLE Fila');
        console.log('   ✅ Fila truncada');
        
        await connection.query('TRUNCATE TABLE Usuario');
        console.log('   ✅ Usuario truncada');
        
        // Reactivar verificación de foreign keys
        await connection.query('SET FOREIGN_KEY_CHECKS = 1');
        
        // Verificar que las tablas están vacías
        const [turnoCount] = await connection.query('SELECT COUNT(*) as count FROM Turno');
        const [filaCount] = await connection.query('SELECT COUNT(*) as count FROM Fila');
        const [usuarioCount] = await connection.query('SELECT COUNT(*) as count FROM Usuario');
        
        await connection.commit();
        
        console.log('\n📊 Verificación:');
        console.log(`   Turno: ${turnoCount[0].count} registros`);
        console.log(`   Fila: ${filaCount[0].count} registros`);
        console.log(`   Usuario: ${usuarioCount[0].count} registros`);
        console.log('\n✅ Base de datos truncada correctamente');
        
    } catch (error) {
        await connection.rollback();
        console.error('❌ Error al truncar la base de datos:', error.message);
    } finally {
        connection.release();
        rl.close();
        process.exit(0);
    }
}

// Ejecutar
truncateDatabase();

