package bo.org.citior.sife.dominio;

import java.math.BigDecimal;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FacturaDetalle {

    private Long id;
    private Long facturaId;
    private int linea;
    private String actividadEconomica;
    private String codigoProducto;
    private String descripcion;
    private Long codigoProductoSin;
    private BigDecimal cantidad;
    private BigDecimal precioUnitario;
    private BigDecimal montoDescuento;
    private BigDecimal subTotal;
    private int unidadMedida;
    private String numeroSerie;
    private String numeroImei;
}
