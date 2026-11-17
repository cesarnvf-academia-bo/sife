package bo.org.citior.sife.dominio.dto;

import java.util.List;

public record CuisRespuesta(
        Long empresaId,
        String nit,
        int codigoSucursal,
        int codigoPuntoVenta,
        String cuis,
        String fechaVigencia,
        boolean transaccion,
        List<MensajeCuis> mensajes) {

    public record MensajeCuis(Integer codigo, String descripcion) {
    }
}
