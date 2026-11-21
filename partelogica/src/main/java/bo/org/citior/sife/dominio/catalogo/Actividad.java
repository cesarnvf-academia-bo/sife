package bo.org.citior.sife.dominio.catalogo;

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
public class Actividad {

    private String codigoCaeb;
    private String descripcion;
    private String tipoActividad;
}
