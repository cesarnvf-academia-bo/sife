package bo.org.citior.sife.evento;

import java.math.BigDecimal;

public record FacturaAprobadaEvento(
        Long facturaId,
        long numeroFactura,
        String cuf,
        String clienteNombre,
        String clienteCorreo,
        String fechaEmision,
        BigDecimal montoTotal,
        String codigoRecepcion) {
}
