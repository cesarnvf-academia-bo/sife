package bo.org.citior.sife.dominio;

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
public class PuntoVenta {

    private Long id;
    private Long sucursalId;
    private String codigo;
    private String puntoventa;
    private int tipo;
    private boolean estado;
}
