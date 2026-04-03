CREATE TABLE IF NOT EXISTS espiritu(
    id SERIAL PRIMARY KEY,
    tipo VARCHAR(255) NOT NULL ,
    nombre varchar(255) NOT NULL,
    nivelDeConexion int NOT NULL,
    CONSTRAINT conexion_valida CHECK (nivelDeConexion BETWEEN 0 AND 100)
);
