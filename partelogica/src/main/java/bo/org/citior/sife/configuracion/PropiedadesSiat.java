package bo.org.citior.sife.configuracion;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "siat")
public record PropiedadesSiat(
        String codigosUrl,
        String sincronizacionUrl,
        String operacionesUrl,
        String compraventaUrl,
        int tiempoConexionMs,
        int tiempoRespuestaMs) {
}
