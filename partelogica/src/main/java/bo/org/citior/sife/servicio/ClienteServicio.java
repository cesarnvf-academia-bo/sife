package bo.org.citior.sife.servicio;

import bo.org.citior.sife.dominio.Cliente;
import bo.org.citior.sife.dominio.dto.ClienteRespuesta;
import bo.org.citior.sife.dominio.dto.ClienteSolicitud;
import bo.org.citior.sife.dominio.dto.PaginaRespuesta;
import bo.org.citior.sife.excepcion.ClienteCodigoInternoDuplicadoExcepcion;
import bo.org.citior.sife.excepcion.ClienteNoEncontradoExcepcion;
import bo.org.citior.sife.excepcion.TipoDocumentoIdentidadNoValidoExcepcion;
import bo.org.citior.sife.mapeador.ClienteMapeador;
import bo.org.citior.sife.mapper.ClienteMapper;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ClienteServicio {

    private static final Set<Integer> TAMANOS_PERMITIDOS = Set.of(5, 10, 15, 20);

    private final ClienteMapper clienteMapper;
    private final ClienteMapeador clienteMapeador;
    private final TipoDocumentoIdentidadServicio tipoDocumentoIdentidadServicio;

    @Transactional(readOnly = true)
    public PaginaRespuesta<ClienteRespuesta> listar(String busqueda, int page, int size) {
        validarTamano(size);

        String filtro = normalizar(busqueda);
        int pagina = Math.max(page, 0);
        long totalElements = clienteMapper.contar(filtro);
        List<ClienteRespuesta> contenido = clienteMapper
                .listar(filtro, pagina * size, size).stream()
                .map(clienteMapeador::aRespuesta)
                .toList();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new PaginaRespuesta<>(
                contenido, totalElements, totalPages, pagina, size);
    }

    @Transactional(readOnly = true)
    public ClienteRespuesta obtenerPorId(Long id) {
        return clienteMapeador.aRespuesta(obtenerCliente(id));
    }

    @Transactional
    public ClienteRespuesta adicionar(ClienteSolicitud solicitud) {
        validarTipo(solicitud.tipo());
        validarCodigoInterno(solicitud.codigoInterno().trim(), null);
        Cliente cliente = clienteMapeador.aModelo(solicitud);
        clienteMapper.insert(cliente);
        return clienteMapeador.aRespuesta(cliente);
    }

    @Transactional
    public ClienteRespuesta modificar(Long id, ClienteSolicitud solicitud) {
        Cliente cliente = obtenerCliente(id);
        validarTipo(solicitud.tipo());
        validarCodigoInterno(solicitud.codigoInterno().trim(), id);
        clienteMapeador.actualizar(cliente, solicitud);
        clienteMapper.update(cliente);
        return clienteMapeador.aRespuesta(cliente);
    }

    @Transactional
    public void eliminar(Long id) {
        obtenerCliente(id);
        clienteMapper.delete(id);
    }

    private Cliente obtenerCliente(Long id) {
        Cliente cliente = clienteMapper.findById(id);
        if (cliente == null) {
            throw new ClienteNoEncontradoExcepcion(id);
        }
        return cliente;
    }

    private void validarTipo(Integer codigo) {
        if (!tipoDocumentoIdentidadServicio.existe(codigo)) {
            throw new TipoDocumentoIdentidadNoValidoExcepcion(codigo);
        }
    }

    private void validarCodigoInterno(String codigoInterno, Long id) {
        boolean duplicado = id == null
                ? clienteMapper.existsByCodigoInterno(codigoInterno)
                : clienteMapper.existsByCodigoInternoAndIdNot(codigoInterno, id);
        if (duplicado) {
            throw new ClienteCodigoInternoDuplicadoExcepcion(codigoInterno);
        }
    }

    private void validarTamano(int size) {
        if (!TAMANOS_PERMITIDOS.contains(size)) {
            throw new IllegalArgumentException("El tamaño debe ser 5, 10, 15 o 20.");
        }
    }

    private String normalizar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
