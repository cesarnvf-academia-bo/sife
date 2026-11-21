package bo.org.citior.sife.dominio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record SucursalSolicitud(
        @NotBlank(message = "El código es obligatorio.")
        @Size(max = 20, message = "El código no puede superar 20 caracteres.")
        String codigo,

        @NotBlank(message = "La sucursal es obligatoria.")
        @Size(max = 250, message = "La sucursal no puede superar 250 caracteres.")
        String sucursal,

        @NotNull(message = "El estado es obligatorio.")
        Boolean estado) {
}
