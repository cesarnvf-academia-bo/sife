package bo.org.citior.sife.evento;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class FacturaCorreoConsumidor {

    private final ObjectMapper objectMapper;
    private final CorreoFacturaServicio correoFacturaServicio;

    @KafkaListener(topics = "${correo.topico}", groupId = "${spring.kafka.consumer.group-id}")
    public void consumir(String mensaje) {
        try {
            FacturaAprobadaEvento evento =
                    objectMapper.readValue(mensaje, FacturaAprobadaEvento.class);
            correoFacturaServicio.enviar(evento);
        } catch (JacksonException excepcion) {
            log.error("No se pudo deserializar el evento de correo.", excepcion);
        } catch (RuntimeException excepcion) {
            log.error("No se pudo procesar el evento de correo.", excepcion);
        }
    }
}
