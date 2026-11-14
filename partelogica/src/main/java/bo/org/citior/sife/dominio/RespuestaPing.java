package bo.org.citior.sife.dominio;

import java.time.Instant;

public record RespuestaPing(String estado, String mensaje, Instant marcaDeTiempo) {
}
