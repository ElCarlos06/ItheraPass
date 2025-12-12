--PARA el carlo tonto cree la base en esecuele
-- Crear la base de datos
CREATE DATABASE IF NOT EXISTS itherapass CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE itherapass;

-- 1. TABLA USUARIO
CREATE TABLE IF NOT EXISTS Usuario (
    id VARCHAR(100) PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    correo VARCHAR(150) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 2. TABLA FILA
CREATE TABLE IF NOT EXISTS Fila (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    categoria VARCHAR(50) NOT NULL,
    estado VARCHAR(20) DEFAULT 'ABIERTA',
    tiempoPromedioAtencion INT DEFAULT 5,
    cantidadEnEspera INT DEFAULT 0,
    cantidadAtendidos INT DEFAULT 0,
    idPropietario VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 3. TABLA TURNO
CREATE TABLE IF NOT EXISTS Turno (
    id INT AUTO_INCREMENT PRIMARY KEY,
    codigoTurno VARCHAR(20) DEFAULT 'A-00',
    idFila INT NOT NULL,
    idUsuario VARCHAR(100) NOT NULL,
    nombreUsuario VARCHAR(100),
    correoUsuario VARCHAR(150),
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    estado VARCHAR(20) DEFAULT 'EN_ESPERA',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    -- Llaves foráneas
    CONSTRAINT fk_turno_fila FOREIGN KEY (idFila) REFERENCES Fila(id) ON DELETE CASCADE,
    CONSTRAINT fk_turno_usuario FOREIGN KEY (idUsuario) REFERENCES Usuario(id) ON DELETE CASCADE,
    INDEX idx_fila (idFila),
    INDEX idx_usuario (idUsuario),
    INDEX idx_estado (estado)
);

-- Datos de ejemplo (opcional)
-- INSERT INTO Usuario (id, nombre, correo) VALUES ('user1', 'Usuario Ejemplo', 'ejemplo@correo.com');


