package bo.org.citior.sife.servicio;

import bo.org.citior.sife.dominio.Producto;
import bo.org.citior.sife.dominio.dto.PaginaRespuesta;
import bo.org.citior.sife.dominio.dto.ProductoRespuesta;
import bo.org.citior.sife.dominio.dto.ProductoSolicitud;
import bo.org.citior.sife.excepcion.CatalogoProductoNoValidoExcepcion;
import bo.org.citior.sife.excepcion.ProductoCodigoInternoDuplicadoExcepcion;
import bo.org.citior.sife.excepcion.ProductoNoEncontradoExcepcion;
import bo.org.citior.sife.mapeador.ProductoMapeador;
import bo.org.citior.sife.mapper.ProductoMapper;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductoGestionServicio {

    private static final Set<Integer> TAMANOS_PERMITIDOS = Set.of(5, 10, 15, 20);

    private final ProductoMapper productoMapper;
    private final ProductoMapeador productoMapeador;
    private final CatalogoProductoServicio catalogoProductoServicio;

    @Transactional(readOnly = true)
    public PaginaRespuesta<ProductoRespuesta> listar(String busqueda, int page, int size) {
        validarTamano(size);

        String filtro = normalizar(busqueda);
        int pagina = Math.max(page, 0);
        long totalElements = productoMapper.contar(filtro);
        List<ProductoRespuesta> contenido = productoMapper
                .listar(filtro, pagina * size, size).stream()
                .map(productoMapeador::aRespuesta)
                .toList();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new PaginaRespuesta<>(
                contenido, totalElements, totalPages, pagina, size);
    }

    @Transactional(readOnly = true)
    public ProductoRespuesta obtenerPorId(Long id) {
        return productoMapeador.aRespuesta(obtenerProducto(id));
    }

    @Transactional
    public ProductoRespuesta adicionar(ProductoSolicitud solicitud) {
        validarCatalogos(solicitud);
        validarCodigoInterno(solicitud.codigoInterno().trim(), null);

        Producto producto = productoMapeador.aModelo(solicitud);
        productoMapper.insert(producto);
        return productoMapeador.aRespuesta(producto);
    }

    @Transactional
    public ProductoRespuesta modificar(Long id, ProductoSolicitud solicitud) {
        Producto producto = obtenerProducto(id);
        validarCatalogos(solicitud);
        validarCodigoInterno(solicitud.codigoInterno().trim(), id);

        productoMapeador.actualizar(producto, solicitud);
        productoMapper.update(producto);
        return productoMapeador.aRespuesta(producto);
    }

    @Transactional
    public void eliminar(Long id) {
        obtenerProducto(id);
        productoMapper.delete(id);
    }

    private Producto obtenerProducto(Long id) {
        Producto producto = productoMapper.findById(id);
        if (producto == null) {
            throw new ProductoNoEncontradoExcepcion(id);
        }
        return producto;
    }

    private void validarCatalogos(ProductoSolicitud solicitud) {
        if (!catalogoProductoServicio.existeActividad(solicitud.codigoActividad().trim())) {
            throw new CatalogoProductoNoValidoExcepcion(
                    "codigoActividad", solicitud.codigoActividad());
        }
        if (!catalogoProductoServicio.existeProductoServicio(solicitud.codigoProducto())) {
            throw new CatalogoProductoNoValidoExcepcion(
                    "codigoProducto", solicitud.codigoProducto());
        }
        if (!catalogoProductoServicio.existeUnidadMedida(solicitud.unidadMedida())) {
            throw new CatalogoProductoNoValidoExcepcion(
                    "unidadMedida", solicitud.unidadMedida());
        }
    }

    private void validarCodigoInterno(String codigoInterno, Long id) {
        boolean duplicado = id == null
                ? productoMapper.existsByCodigoInterno(codigoInterno)
                : productoMapper.existsByCodigoInternoAndIdNot(codigoInterno, id);
        if (duplicado) {
            throw new ProductoCodigoInternoDuplicadoExcepcion(codigoInterno);
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
