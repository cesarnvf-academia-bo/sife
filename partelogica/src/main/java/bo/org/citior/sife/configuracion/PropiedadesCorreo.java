package bo.org.citior.sife.configuracion;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "correo")
public record PropiedadesCorreo(
        String archivoCredenciales,
        String host,
        int puerto,
        String remitente,
        String asunto,
        String topico,
        int codigoAprobado) {
}
