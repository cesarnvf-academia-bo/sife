package bo.org.citior.sife.dominio.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ClienteSolicitud(
        @NotBlank(message = "El código interno es obligatorio.")
        @Size(max = 50, message = "El código interno no puede superar 50 caracteres.")
        String codigoInterno,

        @NotBlank(message = "El cliente es obligatorio.")
        @Size(max = 250, message = "El cliente no puede superar 250 caracteres.")
        String cliente,

        @NotBlank(message = "El DIT es obligatorio.")
        @Size(max = 50, message = "El DIT no puede superar 50 caracteres.")
        String dit,

        @NotNull(message = "El tipo es obligatorio.")
        Integer tipo,

        @NotBlank(message = "El correo es obligatorio.")
        @Email(message = "El correo no es válido.")
        @Size(max = 250, message = "El correo no puede superar 250 caracteres.")
        String correo) {
}
