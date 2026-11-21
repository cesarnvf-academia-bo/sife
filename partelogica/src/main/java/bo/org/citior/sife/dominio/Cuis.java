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
public class Cuis {

    private Long id;
    private Long empresaId;
    private Long sucursalId;
    private Long puntoventaId;
    private int codigoSucursal;
    private int codigoPuntoVenta;
    private String cuis;
    private Instant fechaVigencia;
    private boolean transaccion;
    private String mensajes;
    private boolean estado;
}
