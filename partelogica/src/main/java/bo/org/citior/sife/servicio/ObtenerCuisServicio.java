package bo.org.citior.sife.servicio;

import bo.gob.impuesto.siat.codigos.MensajeServicio;
import bo.gob.impuesto.siat.codigos.ObjectFactory;
import bo.gob.impuesto.siat.codigos.RespuestaCuis;
import bo.gob.impuesto.siat.codigos.ServicioFacturacionCodigos;
import bo.gob.impuesto.siat.codigos.SolicitudCuis;
import bo.org.citior.sife.configuracion.PropiedadesSiat;
import bo.org.citior.sife.dominio.Empresa;
import bo.org.citior.sife.dominio.dto.CuisRespuesta;
import bo.org.citior.sife.dominio.dto.CuisSolicitud;
import bo.org.citior.sife.excepcion.ConfiguracionSiatInvalidaExcepcion;
import bo.org.citior.sife.excepcion.EmpresaNoEncontradaExcepcion;
import bo.org.citior.sife.excepcion.SiatNoDisponibleExcepcion;
import bo.org.citior.sife.excepcion.TokenSiatNoConfiguradoExcepcion;
import bo.org.citior.sife.mapper.EmpresaMapper;
import bo.org.citior.sife.siat.ClienteSiatFactory;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ObtenerCuisServicio {

    private static final int CODIGO_SUCURSAL = 0;
    private static final int CODIGO_CUIS_VIGENTE_EXISTENTE = 980;

    private final EmpresaMapper empresaMapper;
    private final ClienteSiatFactory clienteSiatFactory;
    private final PropiedadesSiat propiedades;

    public CuisRespuesta obtener(Long empresaId, CuisSolicitud solicitud) {
        return obtener(empresaId, CODIGO_SUCURSAL, solicitud.codigoPuntoVenta());
    }

    public CuisRespuesta obtener(Long empresaId, int codigoSucursal, int codigoPuntoVenta) {
        Empresa empresa = empresaMapper.findById(empresaId);
        if (empresa == null) {
            throw new EmpresaNoEncontradaExcepcion(empresaId);
        }

        if (empresa.getToken() == null || empresa.getToken().isBlank()) {
            throw new TokenSiatNoConfiguradoExcepcion(empresaId);
        }

        String codigoSistema = empresa.getCodigoSistema();
        if (codigoSistema == null || codigoSistema.isBlank()) {
            throw new ConfiguracionSiatInvalidaExcepcion(empresaId, "codigoSistema");
        }

        int codigoAmbiente = parsearEntero(
                empresaId, "ambiente", empresa.getAmbiente());
        int codigoModalidad = parsearEntero(
                empresaId, "modalidad", empresa.getModalidad());
        long nit = parsearNit(empresaId, empresa.getNit());

        SolicitudCuis solicitudSiat = new SolicitudCuis();
        solicitudSiat.setCodigoAmbiente(codigoAmbiente);
        solicitudSiat.setCodigoModalidad(codigoModalidad);
        solicitudSiat.setCodigoSistema(codigoSistema);
        solicitudSiat.setCodigoSucursal(codigoSucursal);
        solicitudSiat.setNit(nit);
        solicitudSiat.setCodigoPuntoVenta(new ObjectFactory()
                .createSolicitudCuisCodigoPuntoVenta(codigoPuntoVenta));

        try {
            RespuestaCuis respuesta = clienteSiatFactory
                    .crearPuerto(
                            ServicioFacturacionCodigos.class,
                            propiedades.codigosUrl(),
                            empresa.getToken().trim())
                    .cuis(solicitudSiat);

            return new CuisRespuesta(
                    empresa.getId(),
                    empresa.getNit(),
                    codigoSucursal,
                    codigoPuntoVenta,
                    respuesta.getCodigo(),
                    respuesta.getFechaVigencia() == null
                            ? null
                            : respuesta.getFechaVigencia().toXMLFormat(),
                    esTransaccionValida(respuesta),
                    mensajes(respuesta).stream()
                            .map(mensaje -> new CuisRespuesta.MensajeCuis(
                                    mensaje.getCodigo(),
                                    mensaje.getDescripcion()))
                            .toList());
        } catch (Exception excepcion) {
            throw new SiatNoDisponibleExcepcion(
                    "FacturacionCodigos", mensajeError(excepcion));
        }
    }

    private boolean esTransaccionValida(RespuestaCuis respuesta) {
        if (Boolean.TRUE.equals(respuesta.isTransaccion())) {
            return true;
        }

        String cuis = respuesta.getCodigo();
        return cuis != null
                && !cuis.isBlank()
                && mensajes(respuesta).stream()
                        .anyMatch(mensaje -> mensaje != null
                                && Integer.valueOf(CODIGO_CUIS_VIGENTE_EXISTENTE)
                                        .equals(mensaje.getCodigo()));
    }

    private List<MensajeServicio> mensajes(RespuestaCuis respuesta) {
        return respuesta.getMensajesList() == null
                ? List.of()
                : respuesta.getMensajesList();
    }

    private int parsearEntero(Long empresaId, String campo, String valor) {
        try {
            return Integer.parseInt(valor);
        } catch (NumberFormatException excepcion) {
            throw new ConfiguracionSiatInvalidaExcepcion(empresaId, campo);
        }
    }

    private long parsearNit(Long empresaId, String nit) {
        try {
            return Long.parseLong(nit);
        } catch (NumberFormatException excepcion) {
            throw new ConfiguracionSiatInvalidaExcepcion(empresaId, "nit");
        }
    }

    private String mensajeError(Exception excepcion) {
        String mensaje = excepcion.getMessage();
        return mensaje == null || mensaje.isBlank()
                ? excepcion.getClass().getSimpleName()
                : mensaje;
    }
}
