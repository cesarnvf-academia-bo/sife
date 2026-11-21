package bo.org.citior.sife.dominio;

import java.time.Instant;
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
public class SincronizacionEjecucion {

    private Long id;
    private Long empresaId;
    private Long sucursalId;
    private Long puntoventaId;
    private String funcion;
    private String estado;
    private boolean transaccion;
    private String mensajes;
    private String fechaHoraSiat;
    private String error;
    private Instant ejecutadoEn;
}
