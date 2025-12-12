-- Script para truncar (vaciar) todas las tablas de la base de datos
-- ⚠️ ADVERTENCIA: Esto eliminará TODOS los datos de las tablas
-- Usar solo para pruebas/desarrollo

USE itherapass;

-- Desactivar verificación de foreign keys temporalmente
SET FOREIGN_KEY_CHECKS = 0;

-- Truncar tablas (en orden inverso a las dependencias)
TRUNCATE TABLE Turno;
TRUNCATE TABLE Fila;
TRUNCATE TABLE Usuario;

-- Reactivar verificación de foreign keys
SET FOREIGN_KEY_CHECKS = 1;

-- Verificar que las tablas están vacías
SELECT 'Turno' as Tabla, COUNT(*) as Registros FROM Turno
UNION ALL
SELECT 'Fila', COUNT(*) FROM Fila
UNION ALL
SELECT 'Usuario', COUNT(*) FROM Usuario;

SELECT '✅ Base de datos truncada correctamente' as Resultado;

