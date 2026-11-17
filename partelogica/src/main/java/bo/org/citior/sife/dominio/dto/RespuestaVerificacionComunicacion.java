package bo.org.citior.sife.dominio.dto;

import java.time.Instant;
import java.util.List;

public record RespuestaVerificacionComunicacion(
        Long empresaId,
        String nit,
        Instant fechaVerificacion,
        List<ResultadoServicioSiat> servicios) {
}
