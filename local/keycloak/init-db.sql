CREATE USER keycloak WITH PASSWORD 'root';
-- 2. Crea la base de datos Y ASÍGNALA como propietario (OWNER) al nuevo usuario
CREATE DATABASE keycloak OWNER keycloak;