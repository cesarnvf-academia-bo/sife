package bo.org.citior.sife.servicio;

import bo.org.citior.sife.dominio.RespuestaPing;
import java.time.Instant;
import org.springframework.stereotype.Service;

@Service
public class PingServicio {

    public RespuestaPing obtenerRespuesta() {
        return new RespuestaPing("activo", "pong", Instant.now());
    }
}
