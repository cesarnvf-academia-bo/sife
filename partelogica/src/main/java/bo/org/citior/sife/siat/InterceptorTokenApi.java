package bo.org.citior.sife.siat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.cxf.message.Message;
import org.apache.cxf.phase.AbstractPhaseInterceptor;
import org.apache.cxf.phase.Phase;

public class InterceptorTokenApi extends AbstractPhaseInterceptor<Message> {

    private static final String NOMBRE_CABECERA = "apiKey";

    private final String token;

    public InterceptorTokenApi(String token) {
        super(Phase.PRE_PROTOCOL);
        this.token = token;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void handleMessage(Message mensaje) {
        Map<String, List<String>> cabeceras =
                (Map<String, List<String>>) mensaje.get(Message.PROTOCOL_HEADERS);

        if (cabeceras == null) {
            cabeceras = new HashMap<>();
            mensaje.put(Message.PROTOCOL_HEADERS, cabeceras);
        }

        cabeceras.put(NOMBRE_CABECERA, List.of("TokenApi " + token.trim()));
    }
}
