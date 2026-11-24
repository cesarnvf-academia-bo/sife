package bo.org.citior.sife.dominio.dto;

import java.util.List;

public record CatalogoFacturaRespuesta(
        List<CatalogoOpcion> sucursales,
        List<CatalogoOpcion> puntosVenta,
        List<CatalogoOpcion> clientes,
        List<CatalogoOpcion> tiposDocumentoIdentidad,
        List<CatalogoOpcion> metodosPago,
        List<CatalogoOpcion> monedas,
        List<CatalogoOpcion> motivosAnulacion) {
}
