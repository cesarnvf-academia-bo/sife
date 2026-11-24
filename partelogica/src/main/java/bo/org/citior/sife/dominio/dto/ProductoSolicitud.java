package bo.org.citior.sife.dominio.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record ProductoSolicitud(
        @NotBlank(message = "El código interno es obligatorio.")
        @Size(max = 50, message = "El código interno no puede superar 50 caracteres.")
        String codigoInterno,

        @NotBlank(message = "El producto es obligatorio.")
        @Size(max = 250, message = "El producto no puede superar 250 caracteres.")
        String producto,

        @NotNull(message = "El precio unitario es obligatorio.")
        @DecimalMin(value = "0", message = "El precio unitario no puede ser negativo.")
        @Digits(integer = 10, fraction = 2, message = "El precio unitario no es válido.")
        BigDecimal precioUnitario,

        @NotBlank(message = "La actividad es obligatoria.")
        String codigoActividad,

        @NotNull(message = "El producto/servicio es obligatorio.")
        Long codigoProducto,

        @NotNull(message = "La unidad de medida es obligatoria.")
        Integer unidadMedida) {
}
