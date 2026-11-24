package bo.org.citior.sife.dominio.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public record FacturaDetalleSolicitud(
        @NotBlank String actividadEconomica,
        @NotNull Long codigoProductoSin,
        @NotBlank String codigoProducto,
        @NotBlank String descripcion,
        @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal cantidad,
        @NotNull Integer unidadMedida,
        @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal precioUnitario,
        BigDecimal montoDescuento,
        @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal subTotal,
        String numeroSerie,
        String numeroImei) {
}
