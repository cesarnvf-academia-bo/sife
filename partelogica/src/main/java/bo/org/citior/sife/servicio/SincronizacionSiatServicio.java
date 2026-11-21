package bo.org.citior.sife.servicio;

import bo.gob.impuesto.siat.sincronizacion.RespuestaConfiguracion;
import bo.gob.impuesto.siat.sincronizacion.ServicioFacturacionSincronizacion;
import bo.gob.impuesto.siat.sincronizacion.SolicitudSincronizacion;
import bo.org.citior.sife.configuracion.PropiedadesSiat;
import bo.org.citior.sife.dominio.Empresa;
import bo.org.citior.sife.dominio.enumeracion.SincronizacionFuncion;
import bo.org.citior.sife.excepcion.SiatNoDisponibleExcepcion;
import bo.org.citior.sife.siat.ClienteSiatFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SincronizacionSiatServicio {

    private final ClienteSiatFactory clienteSiatFactory;
    private final PropiedadesSiat propiedades;

    public ServicioFacturacionSincronizacion crearPuerto(Empresa empresa) {
        return clienteSiatFactory.crearPuerto(
                ServicioFacturacionSincronizacion.class,
                propiedades.sincronizacionUrl(),
                empresa.getToken().trim());
    }

    public RespuestaConfiguracion invocar(
            ServicioFacturacionSincronizacion puerto,
            SincronizacionFuncion funcion,
            SolicitudSincronizacion solicitud) {
        try {
            return switch (funcion) {
                case MOTIVO_ANULACION -> puerto.sincronizarParametricaMotivoAnulacion(solicitud);
                case ACTIVIDADES -> puerto.sincronizarActividades(solicitud);
                case FECHA_HORA -> puerto.sincronizarFechaHora(solicitud);
                case LEYENDAS_FACTURA -> puerto.sincronizarListaLeyendasFactura(solicitud);
                case TIPO_HABITACION -> puerto.sincronizarParametricaTipoHabitacion(solicitud);
                case ACTIVIDAD_DOCUMENTO_SECTOR ->
                        puerto.sincronizarListaActividadesDocumentoSector(solicitud);
                case TIPO_DOCUMENTO_IDENTIDAD ->
                        puerto.sincronizarParametricaTipoDocumentoIdentidad(solicitud);
                case UNIDAD_MEDIDA -> puerto.sincronizarParametricaUnidadMedida(solicitud);
                case TIPO_DOCUMENTO_SECTOR ->
                        puerto.sincronizarParametricaTipoDocumentoSector(solicitud);
                case TIPOS_FACTURA -> puerto.sincronizarParametricaTiposFactura(solicitud);
                case MENSAJES_SERVICIOS -> puerto.sincronizarListaMensajesServicios(solicitud);
                case TIPO_METODO_PAGO -> puerto.sincronizarParametricaTipoMetodoPago(solicitud);
                case EVENTOS_SIGNIFICATIVOS ->
                        puerto.sincronizarParametricaEventosSignificativos(solicitud);
                case TIPO_PUNTO_VENTA -> puerto.sincronizarParametricaTipoPuntoVenta(solicitud);
                case PRODUCTOS_SERVICIOS -> puerto.sincronizarListaProductosServicios(solicitud);
                case TIPO_EMISION -> puerto.sincronizarParametricaTipoEmision(solicitud);
                case PAIS_ORIGEN -> puerto.sincronizarParametricaPaisOrigen(solicitud);
                case TIPO_MONEDA -> puerto.sincronizarParametricaTipoMoneda(solicitud);
            };
        } catch (Exception excepcion) {
            throw new SiatNoDisponibleExcepcion(
                    "FacturacionSincronizacion", mensajeError(excepcion));
        }
    }

    private String mensajeError(Exception excepcion) {
        String mensaje = excepcion.getMessage();
        return mensaje == null || mensaje.isBlank()
                ? excepcion.getClass().getSimpleName()
                : mensaje;
    }
}
