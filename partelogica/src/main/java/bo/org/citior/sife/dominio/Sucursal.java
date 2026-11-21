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
public class Sucursal {

    private Long id;
    private String codigo;
    private String sucursal;
    private boolean estado;
}
