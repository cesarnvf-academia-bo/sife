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
public class Cliente {

    private Long id;
    private String codigoInterno;
    private String cliente;
    private String dit;
    private int tipo;
    private String correo;
}
