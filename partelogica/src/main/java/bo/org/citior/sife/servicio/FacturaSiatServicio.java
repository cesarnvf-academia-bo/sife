package bo.org.citior.sife.servicio;

import bo.gob.impuesto.siat.compraventa.MensajeRecepcion;
import bo.gob.impuesto.siat.compraventa.RespuestaRecepcion;
import bo.gob.impuesto.siat.compraventa.ServicioFacturacion;
import bo.gob.impuesto.siat.compraventa.SolicitudAnulacion;
import bo.gob.impuesto.siat.compraventa.SolicitudRecepcion;
import bo.gob.impuesto.siat.compraventa.SolicitudRecepcionFactura;
import bo.gob.impuesto.siat.compraventa.SolicitudReversionAnulacion;
import bo.org.citior.sife.configuracion.PropiedadesSiat;
import bo.org.citior.sife.dominio.Cufd;
import bo.org.citior.sife.dominio.Cuis;
import bo.org.citior.sife.dominio.Empresa;
import bo.org.citior.sife.dominio.Factura;
import bo.org.citior.sife.dominio.dto.MensajeSiat;
import bo.org.citior.sife.dominio.dto.RespuestaSiat;
import bo.org.citior.sife.excepcion.ConfiguracionSiatInvalidaExcepcion;
import bo.org.citior.sife.excepcion.SiatNoDisponibleExcepcion;
import bo.org.citior.sife.excepcion.TokenSiatNoConfiguradoExcepcion;
import bo.org.citior.sife.siat.ClienteSiatFactory;
import jakarta.xml.ws.soap.SOAPFaultException;
import java.net.http.HttpTimeoutException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FacturaSiatServicio {

    private static final int CODIGO_DOCUMENTO_SECTOR = 1;
    private static final DateTimeFormatter FECHA_ENVIO_FORMAT = DateTimeFormatter
            .ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS", Locale.ROOT)
            .withZone(ZoneOffset.UTC);

    private final ClienteSiatFactory clienteSiatFactory;
    private final PropiedadesSiat propiedades;

    public RespuestaSiat recibir(
            Factura factura,
            Empresa empresa,
            Cuis cuis,
            Cufd cufd,
            byte[] gzip,
            String hashArchivo,
            Instant fechaEnvio) {
        if (fechaEnvio == null) {
            throw new IllegalArgumentException("fechaEnvio es obligatoria.");
        }

        SolicitudRecepcionFactura solicitud = new SolicitudRecepcionFactura();
        prepararSolicitud(solicitud, factura, empresa, cuis, cufd);
        solicitud.setArchivo(gzip);
        solicitud.setFechaEnvio(FECHA_ENVIO_FORMAT.format(fechaEnvio));
        solicitud.setHashArchivo(hashArchivo);

        return ejecutar("recepcionFactura", empresa, puerto -> puerto.recepcionFactura(solicitud));
    }

    public RespuestaSiat anular(
            Factura factura,
            Empresa empresa,
            Cuis cuis,
            Cufd cufd,
            Integer codigoMotivo) {
        if (codigoMotivo == null) {
            throw new IllegalArgumentException("codigoMotivo es obligatorio.");
        }

        SolicitudAnulacion solicitud = new SolicitudAnulacion();
        prepararSolicitud(solicitud, factura, empresa, cuis, cufd);
        solicitud.setCodigoMotivo(codigoMotivo);
        solicitud.setCuf(factura.getCuf());

        return ejecutar("anulacionFactura", empresa, puerto -> puerto.anulacionFactura(solicitud));
    }

    public RespuestaSiat revertir(Factura factura, Empresa empresa, Cuis cuis, Cufd cufd) {
        SolicitudReversionAnulacion solicitud = new SolicitudReversionAnulacion();
        prepararSolicitud(solicitud, factura, empresa, cuis, cufd);
        solicitud.setCuf(factura.getCuf());

        return ejecutar(
                "reversionAnulacionFactura",
                empresa,
                puerto -> puerto.reversionAnulacionFactura(solicitud));
    }

    private void prepararSolicitud(
            SolicitudRecepcion solicitud,
            Factura factura,
            Empresa empresa,
            Cuis cuis,
            Cufd cufd) {
        if (factura == null || empresa == null || cuis == null || cufd == null) {
            throw new IllegalArgumentException(
                    "factura, empresa, cuis y cufd son obligatorios.");
        }

        solicitud.setCodigoAmbiente(factura.getCodigoAmbiente());
        solicitud.setCodigoDocumentoSector(CODIGO_DOCUMENTO_SECTOR);
        solicitud.setCodigoEmision(factura.getCodigoEmision());
        solicitud.setCodigoModalidad(factura.getCodigoModalidad());
        solicitud.setCodigoPuntoVenta(factura.getCodigoPuntoVenta());
        solicitud.setCodigoSistema(empresa.getCodigoSistema());
        solicitud.setCodigoSucursal(factura.getCodigoSucursal());
        solicitud.setCufd(cufd.getCufd());
        solicitud.setCuis(cuis.getCuis());
        solicitud.setNit(parsearNit(empresa));
        solicitud.setTipoFacturaDocumento(factura.getTipoFacturaDocumento());
    }

    private RespuestaSiat ejecutar(
            String operacion,
            Empresa empresa,
            Function<ServicioFacturacion, RespuestaRecepcion> llamada) {
        validarToken(empresa);
        ServicioFacturacion puerto = clienteSiatFactory.crearPuerto(
                ServicioFacturacion.class, propiedades.compraventaUrl(), empresa.getToken());

        RespuestaRecepcion respuesta;
        try {
            respuesta = llamada.apply(puerto);
        } catch (SOAPFaultException excepcion) {
            throw new SiatNoDisponibleExcepcion(operacion, excepcion.getMessage());
        } catch (RuntimeException excepcion) {
            throw new SiatNoDisponibleExcepcion(
                    operacion, detalleError(excepcion));
        }
        return convertir(respuesta);
    }

    private String detalleError(RuntimeException excepcion) {
        Throwable causa = excepcion;
        while (causa != null) {
            if (causa instanceof HttpTimeoutException) {
                return "El SIAT no respondió dentro de "
                        + propiedades.tiempoRespuestaMs() + " ms.";
            }
            causa = causa.getCause();
        }
        return excepcion.getMessage() == null
                ? excepcion.getClass().getSimpleName()
                : excepcion.getMessage();
    }

    private void validarToken(Empresa empresa) {
        if (empresa == null || empresa.getToken() == null || empresa.getToken().isBlank()) {
            throw new TokenSiatNoConfiguradoExcepcion(
                    empresa == null ? null : empresa.getId());
        }
    }

    private long parsearNit(Empresa empresa) {
        String nit = empresa.getNit();
        if (nit == null || nit.isBlank()) {
            throw new ConfiguracionSiatInvalidaExcepcion(empresa.getId(), "nit");
        }
        try {
            return Long.parseLong(nit.trim());
        } catch (NumberFormatException excepcion) {
            throw new ConfiguracionSiatInvalidaExcepcion(empresa.getId(), "nit");
        }
    }

    private RespuestaSiat convertir(RespuestaRecepcion respuesta) {
        if (respuesta == null) {
            throw new SiatNoDisponibleExcepcion(
                    "respuestaRecepcion", "El SIAT no devolvió una respuesta.");
        }

        List<MensajeRecepcion> recibidos = respuesta.getMensajesList();
        List<MensajeSiat> mensajes = recibidos == null
                ? List.of()
                : recibidos.stream()
                        .map(mensaje -> new MensajeSiat(
                                mensaje.getCodigo(),
                                mensaje.getDescripcion(),
                                mensaje.isAdvertencia(),
                                mensaje.getNumeroArchivo(),
                                mensaje.getNumeroDetalle()))
                        .toList();

        return new RespuestaSiat(
                respuesta.getCodigoDescripcion(),
                respuesta.getCodigoEstado(),
                respuesta.getCodigoRecepcion(),
                respuesta.isTransaccion(),
                mensajes);
    }
}
