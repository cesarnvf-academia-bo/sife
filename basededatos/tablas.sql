CREATE TABLE empresa (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    nit VARCHAR(20),
    razonsocial VARCHAR(250),
    "codigoSistema" VARCHAR(50),
    modalidad VARCHAR(50),
    ambiente VARCHAR(50),
    "rutacodigo" TEXT,
    "rutasincronizacion" TEXT,
    "rutaoperacion" TEXT,
    "rutacompraventa" TEXT,
    token TEXT,
    "rutafirmadigital" TEXT,
    "clavefirmadigital" TEXT
);
