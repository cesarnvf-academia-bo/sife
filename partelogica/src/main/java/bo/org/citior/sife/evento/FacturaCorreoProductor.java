package bo.org.citior.sife.evento;

import bo.org.citior.sife.configuracion.PropiedadesCorreo;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Slf4j
@Service
@RequiredArgsConstructor
public class FacturaCorreoProductor {

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;
    private final PropiedadesCorreo propiedadesCorreo;

    public void publicar(FacturaAprobadaEvento evento) {
        try {
            String mensaje = objectMapper.writeValueAsString(evento);
            CompletableFuture<SendResult<String, String>> resultado = kafkaTemplate.send(
                    propiedadesCorreo.topico(), String.valueOf(evento.facturaId()), mensaje);
            resultado.whenComplete((envio, error) -> registrarResultado(evento, envio, error));
        } catch (JacksonException excepcion) {
            log.error("No se pudo serializar el evento de correo de la factura {}",
                    evento.facturaId(), excepcion);
        } catch (RuntimeException excepcion) {
            log.error("No se pudo publicar el evento de correo de la factura {}",
                    evento.facturaId(), excepcion);
        }
    }

    private void registrarResultado(
            FacturaAprobadaEvento evento,
            SendResult<String, String> envio,
            Throwable error) {
        if (error != null) {
            log.error("Kafka rechazó el evento de correo de la factura {}",
                    evento.facturaId(), error);
            return;
        }
        RecordMetadata metadatos = envio.getRecordMetadata();
        log.info("Evento de correo publicado para la factura {} en {}-{}",
                evento.facturaId(), metadatos.topic(), metadatos.partition());
    }
}
