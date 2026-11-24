package bo.org.citior.sife.excepcion;

import bo.org.citior.sife.dominio.dto.MensajeSiat;
import java.util.List;

public class RespuestaSiatRechazadaExcepcion extends RuntimeException {

    private final String detalle;
    private final List<MensajeSiat> mensajes;

    public RespuestaSiatRechazadaExcepcion(String detalle, List<MensajeSiat> mensajes) {
        super(detalle);
        this.detalle = detalle;
        this.mensajes = mensajes;
    }

    public String getDetalle() {
        return detalle;
    }

    public List<MensajeSiat> getMensajes() {
        return mensajes;
    }
}
