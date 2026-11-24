package bo.org.citior.sife.dominio.dto;

import jakarta.validation.constraints.NotNull;

public record FacturaAnulacionSolicitud(
        @NotNull Integer codigoMotivo) {
}
