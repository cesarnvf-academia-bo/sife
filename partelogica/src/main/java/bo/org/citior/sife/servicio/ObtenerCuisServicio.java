package bo.org.citior.sife.servicio;

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
import bo.org.citior.sife.repositorio.EmpresaRepositorio;
import bo.org.citior.sife.siat.ClienteSiatFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ObtenerCuisServicio {

    private static final int CODIGO_SUCURSAL = 0;

    private final EmpresaRepositorio empresaRepositorio;
    private final ClienteSiatFactory clienteSiatFactory;
    private final PropiedadesSiat propiedades;

    public CuisRespuesta obtener(Long empresaId, CuisSolicitud solicitud) {
        Empresa empresa = empresaRepositorio.findById(empresaId)
                .orElseThrow(() -> new EmpresaNoEncontradaExcepcion(empresaId));

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
        int codigoPuntoVenta = solicitud.codigoPuntoVenta();

        SolicitudCuis solicitudSiat = new SolicitudCuis();
        solicitudSiat.setCodigoAmbiente(codigoAmbiente);
        solicitudSiat.setCodigoModalidad(codigoModalidad);
        solicitudSiat.setCodigoSistema(codigoSistema);
        solicitudSiat.setCodigoSucursal(CODIGO_SUCURSAL);
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
                    CODIGO_SUCURSAL,
                    codigoPuntoVenta,
                    respuesta.getCodigo(),
                    respuesta.getFechaVigencia() == null
                            ? null
                            : respuesta.getFechaVigencia().toXMLFormat(),
                    Boolean.TRUE.equals(respuesta.isTransaccion()),
                    respuesta.getMensajesList().stream()
                            .map(mensaje -> new CuisRespuesta.MensajeCuis(
                                    mensaje.getCodigo(),
                                    mensaje.getDescripcion()))
                            .toList());
        } catch (Exception excepcion) {
            throw new SiatNoDisponibleExcepcion(
                    "FacturacionCodigos", mensajeError(excepcion));
        }
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
