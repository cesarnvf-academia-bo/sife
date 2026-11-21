CREATE TABLE empresa (
    id BIGINT PRIMARY KEY,
    nit VARCHAR(20),
    razon_social VARCHAR(250),
    codigo_sistema VARCHAR(50),
    modalidad VARCHAR(50),
    ambiente VARCHAR(50),
    ruta_codigo TEXT,
    ruta_sincronizacion TEXT,
    ruta_operacion TEXT,
    ruta_compraventa TEXT,
    token TEXT,
    ruta_firma_digital TEXT,
    clave_firma_digital TEXT
);

CREATE TABLE sucursal (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    codigo VARCHAR(20) NOT NULL UNIQUE,
    sucursal VARCHAR(250) NOT NULL,
    estado BOOLEAN NOT NULL DEFAULT TRUE
);

CREATE TABLE puntoventa (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    sucursal_id BIGINT NOT NULL REFERENCES sucursal(id),
    codigo VARCHAR(20) NOT NULL,
    puntoventa VARCHAR(250) NOT NULL,
    tipo SMALLINT NOT NULL DEFAULT 5,
    estado BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_puntoventa_sucursal_codigo UNIQUE (sucursal_id, codigo),
    CONSTRAINT ck_puntoventa_tipo CHECK (tipo BETWEEN 1 AND 6),
    CONSTRAINT ck_puntoventa_codigo CHECK (codigo ~ '^[0-9]+$')
);

CREATE TABLE cuis (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id BIGINT NOT NULL REFERENCES empresa(id),
    sucursal_id BIGINT NOT NULL REFERENCES sucursal(id),
    puntoventa_id BIGINT NOT NULL REFERENCES puntoventa(id),
    codigo_sucursal INTEGER NOT NULL,
    codigo_punto_venta INTEGER NOT NULL,
    cuis VARCHAR(255) NOT NULL,
    fecha_vigencia TIMESTAMPTZ,
    transaccion BOOLEAN NOT NULL DEFAULT FALSE,
    mensajes TEXT NOT NULL DEFAULT '[]',
    estado BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_obtencion TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE cufd (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id BIGINT NOT NULL REFERENCES empresa(id),
    sucursal_id BIGINT NOT NULL REFERENCES sucursal(id),
    puntoventa_id BIGINT NOT NULL REFERENCES puntoventa(id),
    cuis_id BIGINT REFERENCES cuis(id),
    codigo_sucursal INTEGER NOT NULL,
    codigo_punto_venta INTEGER NOT NULL,
    cufd VARCHAR(255) NOT NULL,
    codigo_control VARCHAR(100),
    direccion VARCHAR(250),
    fecha_vigencia TIMESTAMPTZ,
    transaccion BOOLEAN NOT NULL DEFAULT FALSE,
    mensajes TEXT NOT NULL DEFAULT '[]',
    estado BOOLEAN NOT NULL DEFAULT FALSE,
    fecha_obtencion TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX ux_cuis_activo
    ON cuis (empresa_id, puntoventa_id) WHERE estado;

CREATE UNIQUE INDEX ux_cufd_activo
    ON cufd (empresa_id, puntoventa_id) WHERE estado;

CREATE TABLE sincronizacion_ejecucion (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    empresa_id BIGINT NOT NULL REFERENCES empresa(id),
    sucursal_id BIGINT NOT NULL REFERENCES sucursal(id),
    puntoventa_id BIGINT NOT NULL REFERENCES puntoventa(id),
    funcion VARCHAR(80) NOT NULL,
    estado VARCHAR(10) NOT NULL DEFAULT 'EXITOSA',
    transaccion BOOLEAN NOT NULL DEFAULT FALSE,
    mensajes JSONB NOT NULL DEFAULT '[]'::jsonb,
    fecha_hora_siat TEXT,
    error TEXT,
    ejecutado_en TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT ck_sinc_ejecucion_estado
        CHECK (estado IN ('EXITOSA', 'FALLIDA'))
);

CREATE INDEX idx_sinc_ejecucion_catalogo
    ON sincronizacion_ejecucion
    (empresa_id, puntoventa_id, funcion, estado, id DESC);

CREATE TABLE sincronizacion_motivo_anulacion (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ejecucion_id BIGINT NOT NULL
        REFERENCES sincronizacion_ejecucion(id) ON DELETE CASCADE,
    codigo INTEGER NOT NULL,
    descripcion VARCHAR(500),
    CONSTRAINT uq_sinc_motivo_anulacion_ejecucion_codigo
        UNIQUE (ejecucion_id, codigo)
);

CREATE TABLE sincronizacion_tipo_habitacion (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ejecucion_id BIGINT NOT NULL
        REFERENCES sincronizacion_ejecucion(id) ON DELETE CASCADE,
    codigo INTEGER NOT NULL,
    descripcion VARCHAR(500),
    CONSTRAINT uq_sinc_tipo_habitacion_ejecucion_codigo
        UNIQUE (ejecucion_id, codigo)
);

CREATE TABLE sincronizacion_tipo_documento_identidad (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ejecucion_id BIGINT NOT NULL
        REFERENCES sincronizacion_ejecucion(id) ON DELETE CASCADE,
    codigo INTEGER NOT NULL,
    descripcion VARCHAR(500),
    CONSTRAINT uq_sinc_tipo_documento_identidad_ejecucion_codigo
        UNIQUE (ejecucion_id, codigo)
);

CREATE TABLE sincronizacion_unidad_medida (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ejecucion_id BIGINT NOT NULL
        REFERENCES sincronizacion_ejecucion(id) ON DELETE CASCADE,
    codigo INTEGER NOT NULL,
    descripcion VARCHAR(500),
    CONSTRAINT uq_sinc_unidad_medida_ejecucion_codigo
        UNIQUE (ejecucion_id, codigo)
);

CREATE TABLE sincronizacion_tipo_documento_sector (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ejecucion_id BIGINT NOT NULL
        REFERENCES sincronizacion_ejecucion(id) ON DELETE CASCADE,
    codigo INTEGER NOT NULL,
    descripcion VARCHAR(500),
    CONSTRAINT uq_sinc_tipo_documento_sector_ejecucion_codigo
        UNIQUE (ejecucion_id, codigo)
);

CREATE TABLE sincronizacion_tipo_factura (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ejecucion_id BIGINT NOT NULL
        REFERENCES sincronizacion_ejecucion(id) ON DELETE CASCADE,
    codigo INTEGER NOT NULL,
    descripcion VARCHAR(500),
    CONSTRAINT uq_sinc_tipo_factura_ejecucion_codigo
        UNIQUE (ejecucion_id, codigo)
);

CREATE TABLE sincronizacion_mensaje_servicio (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ejecucion_id BIGINT NOT NULL
        REFERENCES sincronizacion_ejecucion(id) ON DELETE CASCADE,
    codigo INTEGER NOT NULL,
    descripcion VARCHAR(500),
    CONSTRAINT uq_sinc_mensaje_servicio_ejecucion_codigo
        UNIQUE (ejecucion_id, codigo)
);

CREATE TABLE sincronizacion_tipo_metodo_pago (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ejecucion_id BIGINT NOT NULL
        REFERENCES sincronizacion_ejecucion(id) ON DELETE CASCADE,
    codigo INTEGER NOT NULL,
    descripcion VARCHAR(500),
    CONSTRAINT uq_sinc_tipo_metodo_pago_ejecucion_codigo
        UNIQUE (ejecucion_id, codigo)
);

CREATE TABLE sincronizacion_evento_significativo (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ejecucion_id BIGINT NOT NULL
        REFERENCES sincronizacion_ejecucion(id) ON DELETE CASCADE,
    codigo INTEGER NOT NULL,
    descripcion VARCHAR(500),
    CONSTRAINT uq_sinc_evento_significativo_ejecucion_codigo
        UNIQUE (ejecucion_id, codigo)
);

CREATE TABLE sincronizacion_tipo_punto_venta (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ejecucion_id BIGINT NOT NULL
        REFERENCES sincronizacion_ejecucion(id) ON DELETE CASCADE,
    codigo INTEGER NOT NULL,
    descripcion VARCHAR(500),
    CONSTRAINT uq_sinc_tipo_punto_venta_ejecucion_codigo
        UNIQUE (ejecucion_id, codigo)
);

CREATE TABLE sincronizacion_tipo_emision (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ejecucion_id BIGINT NOT NULL
        REFERENCES sincronizacion_ejecucion(id) ON DELETE CASCADE,
    codigo INTEGER NOT NULL,
    descripcion VARCHAR(500),
    CONSTRAINT uq_sinc_tipo_emision_ejecucion_codigo
        UNIQUE (ejecucion_id, codigo)
);

CREATE TABLE sincronizacion_pais_origen (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ejecucion_id BIGINT NOT NULL
        REFERENCES sincronizacion_ejecucion(id) ON DELETE CASCADE,
    codigo INTEGER NOT NULL,
    descripcion VARCHAR(500),
    CONSTRAINT uq_sinc_pais_origen_ejecucion_codigo
        UNIQUE (ejecucion_id, codigo)
);

CREATE TABLE sincronizacion_tipo_moneda (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ejecucion_id BIGINT NOT NULL
        REFERENCES sincronizacion_ejecucion(id) ON DELETE CASCADE,
    codigo INTEGER NOT NULL,
    descripcion VARCHAR(500),
    CONSTRAINT uq_sinc_tipo_moneda_ejecucion_codigo
        UNIQUE (ejecucion_id, codigo)
);

CREATE TABLE sincronizacion_actividad (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ejecucion_id BIGINT NOT NULL
        REFERENCES sincronizacion_ejecucion(id) ON DELETE CASCADE,
    codigo_caeb VARCHAR(20) NOT NULL,
    descripcion VARCHAR(500),
    tipo_actividad VARCHAR(20),
    CONSTRAINT uq_sinc_actividad_ejecucion_codigo_tipo
        UNIQUE (ejecucion_id, codigo_caeb, tipo_actividad)
);

CREATE TABLE sincronizacion_fecha_hora (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ejecucion_id BIGINT NOT NULL UNIQUE
        REFERENCES sincronizacion_ejecucion(id) ON DELETE CASCADE,
    fecha_hora_siat TEXT NOT NULL
);

CREATE TABLE sincronizacion_leyenda_factura (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ejecucion_id BIGINT NOT NULL
        REFERENCES sincronizacion_ejecucion(id) ON DELETE CASCADE,
    codigo_actividad VARCHAR(20) NOT NULL,
    descripcion_leyenda VARCHAR(1000),
    CONSTRAINT uq_sinc_leyenda_ejecucion_actividad_descripcion
        UNIQUE (ejecucion_id, codigo_actividad, descripcion_leyenda)
);

CREATE TABLE sincronizacion_actividad_documento_sector (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ejecucion_id BIGINT NOT NULL
        REFERENCES sincronizacion_ejecucion(id) ON DELETE CASCADE,
    codigo_actividad VARCHAR(20) NOT NULL,
    codigo_documento_sector INTEGER NOT NULL,
    tipo_documento_sector VARCHAR(250),
    CONSTRAINT uq_sinc_actividad_documento_sector
        UNIQUE (ejecucion_id, codigo_actividad, codigo_documento_sector,
                tipo_documento_sector)
);

CREATE TABLE sincronizacion_producto_servicio (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    ejecucion_id BIGINT NOT NULL
        REFERENCES sincronizacion_ejecucion(id) ON DELETE CASCADE,
    codigo_actividad VARCHAR(20) NOT NULL,
    codigo_producto BIGINT NOT NULL,
    descripcion_producto VARCHAR(1000),
    CONSTRAINT uq_sinc_producto_servicio_ejecucion
        UNIQUE (ejecucion_id, codigo_actividad, codigo_producto)
);

CREATE TABLE sincronizacion_producto_nandina (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    producto_id BIGINT NOT NULL
        REFERENCES sincronizacion_producto_servicio(id) ON DELETE CASCADE,
    nandina VARCHAR(100) NOT NULL,
    CONSTRAINT uq_sinc_producto_nandina UNIQUE (producto_id, nandina)
);
