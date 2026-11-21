package bo.org.citior.sife.servicio;

import bo.gob.impuesto.siat.codigos.ObjectFactory;
import bo.gob.impuesto.siat.codigos.RespuestaCufd;
import bo.gob.impuesto.siat.codigos.ServicioFacturacionCodigos;
import bo.gob.impuesto.siat.codigos.SolicitudCufd;
import bo.org.citior.sife.configuracion.PropiedadesSiat;
import bo.org.citior.sife.dominio.Empresa;
import bo.org.citior.sife.dominio.dto.CufdRespuesta;
import bo.org.citior.sife.dominio.dto.CufdSolicitud;
import bo.org.citior.sife.dominio.dto.CuisRespuesta;
import bo.org.citior.sife.dominio.dto.CuisSolicitud;
import bo.org.citior.sife.excepcion.ConfiguracionSiatInvalidaExcepcion;
import bo.org.citior.sife.excepcion.EmpresaNoEncontradaExcepcion;
import bo.org.citior.sife.excepcion.SiatNoDisponibleExcepcion;
import bo.org.citior.sife.excepcion.TokenSiatNoConfiguradoExcepcion;
import bo.org.citior.sife.mapper.EmpresaMapper;
import bo.org.citior.sife.siat.ClienteSiatFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ObtenerCufdServicio {

    private static final int CODIGO_SUCURSAL = 0;

    private final EmpresaMapper empresaMapper;
    private final ObtenerCuisServicio obtenerCuisServicio;
    private final ClienteSiatFactory clienteSiatFactory;
    private final PropiedadesSiat propiedades;

    public CufdRespuesta obtener(Long empresaId, CufdSolicitud solicitud) {
        validarEmpresa(empresaId);
        CuisRespuesta cuisRespuesta = obtenerCuisServicio.obtener(
                empresaId, new CuisSolicitud(solicitud.codigoPuntoVenta()));
        return obtenerConCuis(
                empresaId, CODIGO_SUCURSAL, solicitud.codigoPuntoVenta(), cuisRespuesta);
    }

    public CufdRespuesta obtener(Long empresaId, int codigoSucursal, int codigoPuntoVenta) {
        validarEmpresa(empresaId);
        CuisRespuesta cuisRespuesta = obtenerCuisServicio.obtener(
                empresaId, codigoSucursal, codigoPuntoVenta);
        return obtenerConCuis(
                empresaId, codigoSucursal, codigoPuntoVenta, cuisRespuesta);
    }

    public CufdRespuesta obtenerConCuis(
            Long empresaId,
            int codigoSucursal,
            int codigoPuntoVenta,
            CuisRespuesta cuisRespuesta) {

        DatosEmpresa datos = validarEmpresa(empresaId);
        String cuis = cuisRespuesta == null || cuisRespuesta.cuis() == null
                ? null
                : cuisRespuesta.cuis().trim();
        if (cuis == null || cuis.isBlank()) {
            throw new SiatNoDisponibleExcepcion(
                    "FacturacionCodigos",
                    "No se obtuvo un CUIS válido para el punto de venta "
                            + codigoPuntoVenta + ".");
        }

        SolicitudCufd solicitudSiat = new SolicitudCufd();
        solicitudSiat.setCodigoAmbiente(datos.ambiente());
        solicitudSiat.setCodigoModalidad(datos.modalidad());
        solicitudSiat.setCodigoSistema(datos.empresa().getCodigoSistema());
        solicitudSiat.setCodigoSucursal(codigoSucursal);
        solicitudSiat.setCuis(cuis);
        solicitudSiat.setNit(datos.nit());
        solicitudSiat.setCodigoPuntoVenta(new ObjectFactory()
                .createSolicitudCufdCodigoPuntoVenta(codigoPuntoVenta));

        try {
            RespuestaCufd respuesta = clienteSiatFactory
                    .crearPuerto(
                            ServicioFacturacionCodigos.class,
                            propiedades.codigosUrl(),
                            datos.empresa().getToken().trim())
                    .cufd(solicitudSiat);

            return new CufdRespuesta(
                    datos.empresa().getId(),
                    datos.empresa().getNit(),
                    codigoSucursal,
                    codigoPuntoVenta,
                    respuesta.getCodigo(),
                    respuesta.getCodigoControl(),
                    respuesta.getDireccion(),
                    respuesta.getFechaVigencia() == null
                            ? null
                            : respuesta.getFechaVigencia().toXMLFormat(),
                    Boolean.TRUE.equals(respuesta.isTransaccion()),
                    respuesta.getMensajesList().stream()
                            .map(mensaje -> new CufdRespuesta.MensajeCufd(
                                    mensaje.getCodigo(),
                                    mensaje.getDescripcion()))
                            .toList());
        } catch (Exception excepcion) {
            throw new SiatNoDisponibleExcepcion(
                    "FacturacionCodigos", mensajeError(excepcion));
        }
    }

    private DatosEmpresa validarEmpresa(Long empresaId) {
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

        return new DatosEmpresa(
                empresa,
                parsearEntero(empresaId, "ambiente", empresa.getAmbiente()),
                parsearEntero(empresaId, "modalidad", empresa.getModalidad()),
                parsearNit(empresaId, empresa.getNit()));
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

    private record DatosEmpresa(Empresa empresa, int ambiente, int modalidad, long nit) {
    }
}
