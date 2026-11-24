package bo.org.citior.sife.evento;

import bo.org.citior.sife.configuracion.PropiedadesCorreo;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Slf4j
@Service
@RequiredArgsConstructor
public class CorreoFacturaServicio {

    private static final String PLANTILLA = "correo/factura-aprobada";

    private final JavaMailSenderImpl javaMailSender;
    private final TemplateEngine templateEngine;
    private final PropiedadesCorreo propiedadesCorreo;

    public void enviar(FacturaAprobadaEvento evento) {
        if (evento.clienteCorreo() == null || evento.clienteCorreo().isBlank()) {
            log.warn("La factura {} no tiene correo de cliente; no se envió la notificación.",
                    evento.facturaId());
            return;
        }
        try {
            MimeMessage mensaje = javaMailSender.createMimeMessage();
            MimeMessageHelper ayuda = new MimeMessageHelper(
                    mensaje, true, StandardCharsets.UTF_8.name());
            String remitente = propiedadesCorreo.remitente() == null
                    || propiedadesCorreo.remitente().isBlank()
                    ? javaMailSender.getUsername()
                    : propiedadesCorreo.remitente();
            ayuda.setFrom(remitente);
            ayuda.setTo(evento.clienteCorreo());
            ayuda.setSubject(propiedadesCorreo.asunto() + " " + evento.numeroFactura());
            ayuda.setText(renderizar(evento), true);
            javaMailSender.send(mensaje);
            log.info("Correo de factura aprobada enviado para la factura {}.", evento.facturaId());
        } catch (MessagingException excepcion) {
            log.error("No se pudo preparar el correo de la factura {}.", evento.facturaId(), excepcion);
        } catch (RuntimeException excepcion) {
            log.error("No se pudo enviar el correo de la factura {}.", evento.facturaId(), excepcion);
        }
    }

    private String renderizar(FacturaAprobadaEvento evento) {
        Context contexto = new Context();
        contexto.setVariable("evento", evento);
        return templateEngine.process(PLANTILLA, contexto);
    }
}
