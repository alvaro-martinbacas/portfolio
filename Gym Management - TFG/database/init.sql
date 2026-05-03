-- Script de inicialización para PostgreSQL
-- Este script se ejecuta automáticamente al crear el contenedor de base de datos

-- Crear la base de datos si no existe (ya se crea por las variables de entorno)
-- CREATE DATABASE IF NOT EXISTS gestion_gimnasios;

-- Conectar a la base de datos
\c gestion_gimnasios;

-- Las tablas se crean automáticamente por Hibernate/JPA
-- Este script es para datos iniciales si fuera necesario

-- Ejemplo de datos iniciales (opcional)
-- INSERT INTO gimnasio (nombre, direccion, telefono, hora_apertura, hora_cierre) 
-- VALUES ('Gimnasio Central', 'Calle Principal 123', '900000001', '06:00:00', '23:00:00');

-- Mensaje de confirmación
SELECT 'Base de datos inicializada correctamente' as status;