package bo.org.citior.sife.configuracion;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@EnableConfigurationProperties(PropiedadesCorreo.class)
public class ConfiguracionCorreo {

    @Bean
    public JavaMailSenderImpl javaMailSender(PropiedadesCorreo propiedades) throws IOException {
        List<String> credenciales = leerCredenciales(resolverRuta(propiedades.archivoCredenciales()));
        if (credenciales.size() < 2) {
            throw new IllegalStateException(
                    "El archivo de credenciales de correo debe contener usuario y contraseña.");
        }

        JavaMailSenderImpl remitente = new JavaMailSenderImpl();
        remitente.setHost(propiedades.host());
        remitente.setPort(propiedades.puerto());
        remitente.setProtocol("smtp");
        remitente.setUsername(credenciales.get(0));
        remitente.setPassword(credenciales.get(1));
        remitente.setDefaultEncoding(StandardCharsets.UTF_8.name());

        Properties propiedadesSmtp = remitente.getJavaMailProperties();
        propiedadesSmtp.put("mail.smtp.auth", "true");
        propiedadesSmtp.put("mail.smtp.starttls.enable", "true");
        propiedadesSmtp.put("mail.smtp.connectiontimeout", "5000");
        propiedadesSmtp.put("mail.smtp.timeout", "5000");
        propiedadesSmtp.put("mail.smtp.writetimeout", "5000");
        return remitente;
    }

    private List<String> leerCredenciales(Path ruta) throws IOException {
        return Files.readAllLines(ruta, StandardCharsets.UTF_8).stream()
                .map(String::trim)
                .filter(linea -> !linea.isEmpty())
                .toList();
    }

    private Path resolverRuta(String ruta) {
        Path candidato = Path.of(ruta);
        if (candidato.isAbsolute() || Files.exists(candidato)) {
            return candidato;
        }
        Path candidatoRaiz = Path.of("..").resolve(candidato);
        if (Files.exists(candidatoRaiz)) {
            return candidatoRaiz.toAbsolutePath().normalize();
        }
        return candidato.toAbsolutePath().normalize();
    }
}
