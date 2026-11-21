package bo.org.citior.sife.dominio.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record PuntoVentaSolicitud(
        @NotNull(message = "La sucursal es obligatoria.")
        Long sucursalId,

        @NotBlank(message = "El código es obligatorio.")
        @Pattern(regexp = "^[0-9]+$", message = "El código debe ser numérico.")
        @Size(max = 20, message = "El código no puede superar 20 caracteres.")
        String codigo,

        @NotBlank(message = "El punto de venta es obligatorio.")
        @Size(max = 250, message = "El punto de venta no puede superar 250 caracteres.")
        String puntoventa,

        @Min(value = 1, message = "El tipo debe estar entre 1 y 6.")
        @Max(value = 6, message = "El tipo debe estar entre 1 y 6.")
        Integer tipo,

        @NotNull(message = "El estado es obligatorio.")
        Boolean estado) {
}
