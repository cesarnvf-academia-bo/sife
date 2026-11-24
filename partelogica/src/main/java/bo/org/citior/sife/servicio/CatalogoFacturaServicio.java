package bo.org.citior.sife.servicio;

import bo.org.citior.sife.dominio.Cliente;
import bo.org.citior.sife.dominio.PuntoVenta;
import bo.org.citior.sife.dominio.Sucursal;
import bo.org.citior.sife.dominio.dto.CatalogoFacturaRespuesta;
import bo.org.citior.sife.dominio.dto.CatalogoOpcion;
import bo.org.citior.sife.dominio.dto.TipoDocumentoIdentidadRespuesta;
import bo.org.citior.sife.mapper.ClienteMapper;
import bo.org.citior.sife.mapper.PuntoVentaMapper;
import bo.org.citior.sife.mapper.SincronizacionMapper;
import bo.org.citior.sife.mapper.SucursalMapper;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CatalogoFacturaServicio {

    private final SucursalMapper sucursalMapper;
    private final PuntoVentaMapper puntoVentaMapper;
    private final ClienteMapper clienteMapper;
    private final SincronizacionMapper sincronizacionMapper;

    @Transactional(readOnly = true)
    public CatalogoFacturaRespuesta catalogos() {
        List<CatalogoOpcion> sucursales = ordenar(sucursalMapper.listarActivos().stream()
                .map(this::aOpcion)
                .toList());
        List<CatalogoOpcion> puntosVenta = ordenar(puntoVentaMapper.listarActivos().stream()
                .map(this::aOpcion)
                .toList());
        List<CatalogoOpcion> clientes = ordenar(clienteMapper.listarActivos().stream()
                .map(this::aOpcion)
                .toList());

        List<CatalogoOpcion> tiposDocumento = ordenar(sincronizacionMapper
                .listarTiposDocumentoIdentidad().stream()
                .map(this::aOpcion)
                .toList());
        List<CatalogoOpcion> metodosPago =
                ordenar(sincronizacionMapper.listarMetodosPago());
        List<CatalogoOpcion> monedas = ordenar(sincronizacionMapper.listarMonedas());
        List<CatalogoOpcion> motivosAnulacion =
                ordenar(sincronizacionMapper.listarMotivosAnulacion());

        return new CatalogoFacturaRespuesta(
                sucursales,
                puntosVenta,
                clientes,
                tiposDocumento,
                metodosPago,
                monedas,
                motivosAnulacion);
    }

    private CatalogoOpcion aOpcion(Sucursal sucursal) {
        String codigo = Objects.toString(sucursal.getCodigo(), "");
        return new CatalogoOpcion(
                Objects.toString(sucursal.getId(), ""),
                codigo + " - " + Objects.toString(sucursal.getSucursal(), ""));
    }

    private CatalogoOpcion aOpcion(PuntoVenta puntoVenta) {
        String codigo = Objects.toString(puntoVenta.getCodigo(), "");
        return new CatalogoOpcion(
                Objects.toString(puntoVenta.getId(), ""),
                codigo + " - " + Objects.toString(puntoVenta.getPuntoventa(), ""));
    }

    private CatalogoOpcion aOpcion(Cliente cliente) {
        return new CatalogoOpcion(
                Objects.toString(cliente.getId(), ""),
                Objects.toString(cliente.getCodigoInterno(), "")
                        + " - " + Objects.toString(cliente.getCliente(), ""));
    }

    private CatalogoOpcion aOpcion(TipoDocumentoIdentidadRespuesta tipoDocumento) {
        return new CatalogoOpcion(
                Objects.toString(tipoDocumento.codigo(), ""),
                tipoDocumento.descripcion());
    }

    private List<CatalogoOpcion> ordenar(List<CatalogoOpcion> opciones) {
        return opciones.stream()
                .sorted(Comparator.comparing(
                        opcion -> Objects.toString(opcion.descripcion(), ""),
                        String.CASE_INSENSITIVE_ORDER))
                .toList();
    }
}
