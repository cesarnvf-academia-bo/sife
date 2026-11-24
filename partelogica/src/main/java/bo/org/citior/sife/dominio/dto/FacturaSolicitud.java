package bo.org.citior.sife.dominio.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

public record FacturaSolicitud(
        @NotNull Long sucursalId,
        Long puntoventaId,
        @NotNull Long clienteId,
        @NotNull Integer codigoAmbiente,
        @NotNull Integer codigoEmision,
        @NotNull Integer codigoModalidad,
        @NotNull Integer codigoMetodoPago,
        @NotNull Integer tipoFacturaDocumento,
        @NotBlank String fechaEmision,
        @NotBlank String nombreRazonSocial,
        @NotBlank String numeroDocumento,
        String complemento,
        String telefono,
        String numeroTarjeta,
        BigDecimal montoGiftCard,
        BigDecimal descuentoAdicional,
        Integer codigoExcepcion,
        String cafc,
        @NotNull Integer codigoMoneda,
        @NotNull @DecimalMin(value = "0", inclusive = false) BigDecimal tipoCambio,
        @NotBlank String leyenda,
        @NotBlank String usuario,
        @NotEmpty List<@Valid FacturaDetalleSolicitud> detalle,
        Integer codigoTipoDocumentoIdentidad,
        String municipio,
        String direccion) {
}
