package bo.org.citior.sife.dominio.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CuisSolicitud(
        @NotNull(message = "El punto de venta es obligatorio.")
        @Min(value = 0, message = "El punto de venta debe ser 0 o 1.")
        @Max(value = 1, message = "El punto de venta debe ser 0 o 1.")
        Integer codigoPuntoVenta) {
}
