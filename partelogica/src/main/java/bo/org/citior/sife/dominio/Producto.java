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
public class Producto {

    private Long id;
    private String codigoInterno;
    private String producto;
    private BigDecimal precioUnitario;
    private String codigoActividad;
    private Long codigoProducto;
    private Integer unidadMedida;
}
