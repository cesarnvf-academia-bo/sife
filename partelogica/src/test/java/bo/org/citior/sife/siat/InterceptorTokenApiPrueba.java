package bo.org.citior.sife.siat;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.cxf.message.Message;
import org.apache.cxf.message.MessageImpl;
import org.junit.jupiter.api.Test;

class InterceptorTokenApiPrueba {

    @Test
    @SuppressWarnings("unchecked")
    void agregaCabeceraApiKey() {
        Message mensaje = new MessageImpl();

        new InterceptorTokenApi(" token-de-prueba ").handleMessage(mensaje);

        Map<String, List<String>> cabeceras =
                (Map<String, List<String>>) mensaje.get(Message.PROTOCOL_HEADERS);
        assertThat(cabeceras)
                .containsEntry("apiKey", List.of("TokenApi token-de-prueba"));
    }

    @Test
    @SuppressWarnings("unchecked")
    void conservaCabecerasExistentes() {
        Message mensaje = new MessageImpl();
        Map<String, List<String>> cabeceras = new HashMap<>();
        cabeceras.put("Otra", List.of("valor"));
        mensaje.put(Message.PROTOCOL_HEADERS, cabeceras);

        new InterceptorTokenApi("token").handleMessage(mensaje);

        assertThat(cabeceras)
                .containsEntry("Otra", List.of("valor"))
                .containsEntry("apiKey", List.of("TokenApi token"));
    }
}
