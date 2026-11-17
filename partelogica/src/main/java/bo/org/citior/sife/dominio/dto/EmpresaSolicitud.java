package bo.org.citior.sife.dominio.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmpresaSolicitud(
        @NotBlank(message = "El NIT es obligatorio.")
        @Size(max = 20, message = "El NIT no debe superar los 20 caracteres.")
        String nit,

        @NotBlank(message = "La razón social es obligatoria.")
        @Size(max = 250, message = "La razón social no debe superar los 250 caracteres.")
        String razonsocial,

        @Size(max = 50, message = "El código de sistema no debe superar los 50 caracteres.")
        String codigoSistema,

        @Size(max = 50, message = "La modalidad no debe superar los 50 caracteres.")
        String modalidad,

        @Size(max = 50, message = "El ambiente no debe superar los 50 caracteres.")
        String ambiente,

        String rutaCodigo,

        String rutaSincronizacion,

        String rutaOperacion,

        String rutaCompraventa,

        String token,

        String rutaFirmaDigital,

        String claveFirmaDigital
) {
}
