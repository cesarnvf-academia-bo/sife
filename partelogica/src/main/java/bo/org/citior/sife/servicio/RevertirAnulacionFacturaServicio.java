package bo.org.citior.sife.servicio;

import bo.org.citior.sife.dominio.Cufd;
import bo.org.citior.sife.dominio.Cuis;
import bo.org.citior.sife.dominio.Empresa;
import bo.org.citior.sife.dominio.Factura;
import bo.org.citior.sife.dominio.dto.FacturaRespuesta;
import bo.org.citior.sife.dominio.dto.RespuestaSiat;
import bo.org.citior.sife.excepcion.ConfiguracionSiatInvalidaExcepcion;
import bo.org.citior.sife.excepcion.EmpresaNoEncontradaExcepcion;
import bo.org.citior.sife.excepcion.FacturaEstadoInvalidoExcepcion;
import bo.org.citior.sife.excepcion.FacturaNoEncontradaExcepcion;
import bo.org.citior.sife.mapeador.FacturaMapeador;
import bo.org.citior.sife.mapper.CufdMapper;
import bo.org.citior.sife.mapper.CuisMapper;
import bo.org.citior.sife.mapper.EmpresaMapper;
import bo.org.citior.sife.mapper.FacturaDetalleMapper;
import bo.org.citior.sife.mapper.FacturaMapper;
import java.time.Instant;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RevertirAnulacionFacturaServicio {

    private static final String ESTADO_ANULADA = "ANULADA";
    private static final String ESTADO_REVERTIDA = "REVERTIDA";

    private final FacturaMapper facturaMapper;
    private final FacturaDetalleMapper facturaDetalleMapper;
    private final FacturaMapeador facturaMapeador;
    private final EmpresaMapper empresaMapper;
    private final CuisMapper cuisMapper;
    private final CufdMapper cufdMapper;
    private final FacturaSiatServicio facturaSiatServicio;

    @Transactional
    public FacturaRespuesta revertir(Long id) {
        Factura factura = facturaMapper.obtenerPorId(id);
        if (factura == null) {
            throw new FacturaNoEncontradaExcepcion(id);
        }
        if (!ESTADO_ANULADA.equals(factura.getEstado())) {
            throw new FacturaEstadoInvalidoExcepcion(id, ESTADO_ANULADA, factura.getEstado());
        }

        Empresa empresa = empresaMapper.findById(EmpresaServicio.EMPRESA_ID);
        if (empresa == null) {
            throw new EmpresaNoEncontradaExcepcion(EmpresaServicio.EMPRESA_ID);
        }
        if (factura.getPuntoventaId() == null) {
            throw new ConfiguracionSiatInvalidaExcepcion(empresa.getId(), "puntoventa");
        }

        Cuis cuis = cuisMapper.obtenerVigente(empresa.getId(), factura.getPuntoventaId());
        if (cuis == null || cuis.getCuis() == null || cuis.getCuis().isBlank()) {
            throw new ConfiguracionSiatInvalidaExcepcion(empresa.getId(), "cuis");
        }
        Cufd cufd = cufdMapper.obtenerVigente(empresa.getId(), factura.getPuntoventaId());
        if (cufd == null || cufd.getCufd() == null || cufd.getCufd().isBlank()) {
            throw new ConfiguracionSiatInvalidaExcepcion(empresa.getId(), "cufd");
        }

        RespuestaSiat respuesta = facturaSiatServicio.revertir(factura, empresa, cuis, cufd);

        factura.setFechaReversion(Instant.now());
        factura.setReversionTransaccion(respuesta.transaccion());
        factura.setReversionCodigoEstado(respuesta.codigoEstado());
        factura.setReversionCodigoDescripcion(respuesta.codigoDescripcion());
        factura.setReversionMensajes(facturaMapeador.serializarMensajes(respuesta.mensajes()));
        factura.setEstado(
                Boolean.TRUE.equals(respuesta.transaccion()) ? ESTADO_REVERTIDA : ESTADO_ANULADA);

        facturaMapper.actualizarReversion(id, factura);
        return facturaMapeador.aRespuesta(
                factura, facturaDetalleMapper.listarPorFacturaId(id));
    }
}
