INSERT INTO empresa (
    id,
    nit,
    razon_social,
    codigo_sistema,
    modalidad,
    ambiente,
    ruta_codigo,
    ruta_sincronizacion,
    ruta_operacion,
    ruta_compraventa,
    token,
    ruta_firma_digital,
    clave_firma_digital
)
VALUES (
    1,
    '710035027',
    'CONSULTORA Y ASESORAMIENTO FATVAL S.R.L.',
    '228EDC16AF13EA61C753E',
    '1',
    '2',
    'https://pilotosiatservicios.impuestos.gob.bo/v2/FacturacionCodigos?wsdl',
    'https://pilotosiatservicios.impuestos.gob.bo/v2/FacturacionSincronizacion?wsdl',
    'https://pilotosiatservicios.impuestos.gob.bo/v2/FacturacionOperaciones?wsdl',
    'https://pilotosiatservicios.impuestos.gob.bo/v2/ServicioFacturacionCompraVenta?wsdl',
    'eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJmYXR2YWxzcmxAZ21haWwuY29tIiwiY29kaWdvU2lzdGVtYSI6IjIyOEVEQzE2QUYxM0VBNjFDNzUzRSIsIm5pdCI6Ikg0c0lBQUFBQUFBQUFETTNOREF3TmpVd01nY0FncGIxR1FrQUFBQT0iLCJpZCI6NjI5OTgwNywiZXhwIjoxNzk4ODM4NTM5LCJpYXQiOjE3OTQ2MTkzMDksIm5pdERlbGVnYWRvIjo3MTAwMzUwMjcsInN1YnNpc3RlbWEiOiJTRkUifQ.Ffdy0dILDmBcY5RoWVSycnfF_jYCKrHD7nNiB0x3qUxFntHQyuQ6kVVNlOXrC_RbLtGkx6S_bM1DmZ3SVgrj8A',
    '/home/cesarnvf/Capacitacion/sife/generarfirma/sife.p12',
    '123qwe'
);

INSERT INTO sucursal (codigo, sucursal, estado)
VALUES ('0', 'Casa Matriz', TRUE);

INSERT INTO puntoventa (sucursal_id, codigo, puntoventa, estado)
VALUES
    (1, '0', 'Punto Venta Cajeros 0', TRUE),
    (1, '1', 'Punto Venta Cajeros 1', TRUE);
