package bo.org.citior.sife.dominio.enumeracion;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SincronizacionFuncion {

    MOTIVO_ANULACION(
            "sincronizarParametricaMotivoAnulacion",
            "Motivo de anulación",
            "sincronizacion_motivo_anulacion",
            TipoCatalogo.PARAMETRICA),
    ACTIVIDADES(
            "sincronizarActividades",
            "Actividades",
            "sincronizacion_actividad",
            TipoCatalogo.ACTIVIDAD),
    FECHA_HORA(
            "sincronizarFechaHora",
            "Fecha y hora SIAT",
            "sincronizacion_fecha_hora",
            TipoCatalogo.FECHA_HORA),
    LEYENDAS_FACTURA(
            "sincronizarListaLeyendasFactura",
            "Leyendas de factura",
            "sincronizacion_leyenda_factura",
            TipoCatalogo.LEYENDA),
    TIPO_HABITACION(
            "sincronizarParametricaTipoHabitacion",
            "Tipo de habitación",
            "sincronizacion_tipo_habitacion",
            TipoCatalogo.PARAMETRICA),
    ACTIVIDAD_DOCUMENTO_SECTOR(
            "sincronizarListaActividadesDocumentoSector",
            "Actividad por documento y sector",
            "sincronizacion_actividad_documento_sector",
            TipoCatalogo.ACTIVIDAD_DOCUMENTO_SECTOR),
    TIPO_DOCUMENTO_IDENTIDAD(
            "sincronizarParametricaTipoDocumentoIdentidad",
            "Tipo de documento de identidad",
            "sincronizacion_tipo_documento_identidad",
            TipoCatalogo.PARAMETRICA),
    UNIDAD_MEDIDA(
            "sincronizarParametricaUnidadMedida",
            "Unidad de medida",
            "sincronizacion_unidad_medida",
            TipoCatalogo.PARAMETRICA),
    TIPO_DOCUMENTO_SECTOR(
            "sincronizarParametricaTipoDocumentoSector",
            "Tipo de documento por sector",
            "sincronizacion_tipo_documento_sector",
            TipoCatalogo.PARAMETRICA),
    TIPOS_FACTURA(
            "sincronizarParametricaTiposFactura",
            "Tipos de factura",
            "sincronizacion_tipo_factura",
            TipoCatalogo.PARAMETRICA),
    MENSAJES_SERVICIOS(
            "sincronizarListaMensajesServicios",
            "Mensajes de servicios",
            "sincronizacion_mensaje_servicio",
            TipoCatalogo.PARAMETRICA),
    TIPO_METODO_PAGO(
            "sincronizarParametricaTipoMetodoPago",
            "Tipo de método de pago",
            "sincronizacion_tipo_metodo_pago",
            TipoCatalogo.PARAMETRICA),
    EVENTOS_SIGNIFICATIVOS(
            "sincronizarParametricaEventosSignificativos",
            "Eventos significativos",
            "sincronizacion_evento_significativo",
            TipoCatalogo.PARAMETRICA),
    TIPO_PUNTO_VENTA(
            "sincronizarParametricaTipoPuntoVenta",
            "Tipo de punto de venta",
            "sincronizacion_tipo_punto_venta",
            TipoCatalogo.PARAMETRICA),
    PRODUCTOS_SERVICIOS(
            "sincronizarListaProductosServicios",
            "Productos y servicios",
            "sincronizacion_producto_servicio",
            TipoCatalogo.PRODUCTO_SERVICIO),
    TIPO_EMISION(
            "sincronizarParametricaTipoEmision",
            "Tipo de emisión",
            "sincronizacion_tipo_emision",
            TipoCatalogo.PARAMETRICA),
    PAIS_ORIGEN(
            "sincronizarParametricaPaisOrigen",
            "País de origen",
            "sincronizacion_pais_origen",
            TipoCatalogo.PARAMETRICA),
    TIPO_MONEDA(
            "sincronizarParametricaTipoMoneda",
            "Tipo de moneda",
            "sincronizacion_tipo_moneda",
            TipoCatalogo.PARAMETRICA);

    private final String nombre;
    private final String etiqueta;
    private final String tabla;
    private final TipoCatalogo tipo;

    public static SincronizacionFuncion desdeNombre(String nombre) {
        return Arrays.stream(values())
                .filter(funcion -> funcion.nombre.equals(nombre))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException(
                        "Función de sincronización no válida: " + nombre));
    }
}
