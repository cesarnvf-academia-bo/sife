package bo.org.citior.sife.servicio;

import bo.org.citior.sife.dominio.Sucursal;
import bo.org.citior.sife.dominio.dto.PaginaRespuesta;
import bo.org.citior.sife.dominio.dto.SucursalRespuesta;
import bo.org.citior.sife.dominio.dto.SucursalSolicitud;
import bo.org.citior.sife.excepcion.SucursalCodigoDuplicadoExcepcion;
import bo.org.citior.sife.excepcion.SucursalNoEncontradaExcepcion;
import bo.org.citior.sife.mapeador.SucursalMapeador;
import bo.org.citior.sife.mapper.SucursalMapper;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SucursalServicio {

    private static final Set<Integer> TAMANOS_PERMITIDOS = Set.of(5, 10, 15, 20);

    private final SucursalMapper sucursalMapper;
    private final SucursalMapeador sucursalMapeador;

    @Transactional(readOnly = true)
    public PaginaRespuesta<SucursalRespuesta> listar(String busqueda, int page, int size) {
        validarTamano(size);

        String filtro = normalizar(busqueda);
        long totalElements = sucursalMapper.contar(filtro);
        int offset = Math.max(page, 0) * size;
        List<SucursalRespuesta> contenido = sucursalMapper.listar(filtro, offset, size).stream()
                .map(sucursalMapeador::aRespuesta)
                .toList();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new PaginaRespuesta<>(
                contenido, totalElements, totalPages, Math.max(page, 0), size);
    }

    @Transactional(readOnly = true)
    public SucursalRespuesta obtenerPorId(Long id) {
        return sucursalMapeador.aRespuesta(obtenerSucursal(id));
    }

    @Transactional
    public SucursalRespuesta adicionar(SucursalSolicitud solicitud) {
        String codigo = solicitud.codigo().trim();
        if (sucursalMapper.existsByCodigo(codigo)) {
            throw new SucursalCodigoDuplicadoExcepcion(codigo);
        }

        Sucursal sucursal = sucursalMapeador.aModelo(solicitud);
        sucursalMapper.insert(sucursal);
        return sucursalMapeador.aRespuesta(sucursal);
    }

    @Transactional
    public SucursalRespuesta modificar(Long id, SucursalSolicitud solicitud) {
        Sucursal sucursal = obtenerSucursal(id);
        String codigo = solicitud.codigo().trim();
        if (sucursalMapper.existsByCodigoAndIdNot(codigo, id)) {
            throw new SucursalCodigoDuplicadoExcepcion(codigo);
        }

        sucursalMapeador.actualizar(sucursal, solicitud);
        sucursalMapper.update(sucursal);
        return sucursalMapeador.aRespuesta(sucursal);
    }

    @Transactional
    public void eliminar(Long id) {
        obtenerSucursal(id);
        sucursalMapper.delete(id);
    }

    private Sucursal obtenerSucursal(Long id) {
        Sucursal sucursal = sucursalMapper.findById(id);
        if (sucursal == null) {
            throw new SucursalNoEncontradaExcepcion(id);
        }
        return sucursal;
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
