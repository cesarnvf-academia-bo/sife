package bo.org.citior.sife.dominio.catalogo;

import java.util.List;
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
public class ProductoServicio {

    private Long id;
    private Long ejecucionId;
    private String codigoActividad;
    private Long codigoProducto;
    private String descripcionProducto;
    private List<String> nandina;
}
