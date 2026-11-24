package bo.org.citior.sife.dominio.dto;

import java.math.BigDecimal;

public record FacturaDetalleRespuesta(
        Long id,
        int linea,
        String actividadEconomica,
        Long codigoProductoSin,
        String codigoProducto,
        String descripcion,
        BigDecimal cantidad,
        int unidadMedida,
        BigDecimal precioUnitario,
        BigDecimal montoDescuento,
        BigDecimal subTotal,
        String numeroSerie,
        String numeroImei) {
}
