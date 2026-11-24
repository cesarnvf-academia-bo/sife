package bo.org.citior.sife.siat;

import bo.org.citior.sife.configuracion.PropiedadesSiat;
import lombok.RequiredArgsConstructor;
import org.apache.cxf.Bus;
import org.apache.cxf.endpoint.Client;
import org.apache.cxf.frontend.ClientProxy;
import org.apache.cxf.jaxws.JaxWsProxyFactoryBean;
import org.apache.cxf.transport.http.HTTPConduit;
import org.apache.cxf.transports.http.configuration.HTTPClientPolicy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteSiatFactory {

    private final Bus busCxf;
    private final PropiedadesSiat propiedades;

    public <T> T crearPuerto(Class<T> interfaz, String url, String token) {
        JaxWsProxyFactoryBean fabrica = new JaxWsProxyFactoryBean();
        fabrica.setBus(busCxf);
        fabrica.setServiceClass(interfaz);
        fabrica.setAddress(url);

        T puerto = interfaz.cast(fabrica.create());
        Client cliente = ClientProxy.getClient(puerto);

        cliente.getOutInterceptors().add(new InterceptorTokenApi(token));
        configurarTiempos(cliente);

        return puerto;
    }

    private void configurarTiempos(Client cliente) {
        HTTPConduit conducto = (HTTPConduit) cliente.getConduit();
        HTTPClientPolicy politica = conducto.getClient();

        if (politica == null) {
            politica = new HTTPClientPolicy();
            conducto.setClient(politica);
        }

        politica.setConnectionTimeout(propiedades.tiempoConexionMs());
        politica.setReceiveTimeout(propiedades.tiempoRespuestaMs());
        politica.setConnectionRequestTimeout(propiedades.tiempoConexionMs());
    }
}
