package bo.org.citior.sife.mapeador;

import bo.org.citior.sife.dominio.Cliente;
import bo.org.citior.sife.dominio.dto.ClienteRespuesta;
import bo.org.citior.sife.dominio.dto.ClienteSolicitud;
import bo.org.citior.sife.servicio.TipoDocumentoIdentidadServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ClienteMapeador {

    private final TipoDocumentoIdentidadServicio tipoDocumentoIdentidadServicio;

    public Cliente aModelo(ClienteSolicitud solicitud) {
        return Cliente.builder()
                .codigoInterno(solicitud.codigoInterno().trim())
                .cliente(solicitud.cliente().trim())
                .dit(solicitud.dit().trim())
                .tipo(solicitud.tipo())
                .correo(solicitud.correo().trim())
                .build();
    }

    public void actualizar(Cliente cliente, ClienteSolicitud solicitud) {
        cliente.setCodigoInterno(solicitud.codigoInterno().trim());
        cliente.setCliente(solicitud.cliente().trim());
        cliente.setDit(solicitud.dit().trim());
        cliente.setTipo(solicitud.tipo());
        cliente.setCorreo(solicitud.correo().trim());
    }

    public ClienteRespuesta aRespuesta(Cliente cliente) {
        return new ClienteRespuesta(
                cliente.getId(),
                cliente.getCodigoInterno(),
                cliente.getCliente(),
                cliente.getDit(),
                cliente.getTipo(),
                tipoDocumentoIdentidadServicio.descripcion(cliente.getTipo()),
                cliente.getCorreo());
    }
}
