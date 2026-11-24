package bo.org.citior.sife.mapeador;

import bo.org.citior.sife.dominio.Producto;
import bo.org.citior.sife.dominio.dto.ProductoRespuesta;
import bo.org.citior.sife.dominio.dto.ProductoSolicitud;
import bo.org.citior.sife.servicio.CatalogoProductoServicio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProductoMapeador {

    private final CatalogoProductoServicio catalogoProductoServicio;

    public Producto aModelo(ProductoSolicitud solicitud) {
        return Producto.builder()
                .codigoInterno(solicitud.codigoInterno().trim())
                .producto(solicitud.producto().trim())
                .precioUnitario(solicitud.precioUnitario())
                .codigoActividad(solicitud.codigoActividad().trim())
                .codigoProducto(solicitud.codigoProducto())
                .unidadMedida(solicitud.unidadMedida())
                .build();
    }

    public void actualizar(Producto producto, ProductoSolicitud solicitud) {
        producto.setCodigoInterno(solicitud.codigoInterno().trim());
        producto.setProducto(solicitud.producto().trim());
        producto.setPrecioUnitario(solicitud.precioUnitario());
        producto.setCodigoActividad(solicitud.codigoActividad().trim());
        producto.setCodigoProducto(solicitud.codigoProducto());
        producto.setUnidadMedida(solicitud.unidadMedida());
    }

    public ProductoRespuesta aRespuesta(Producto producto) {
        return new ProductoRespuesta(
                producto.getId(),
                producto.getCodigoInterno(),
                producto.getProducto(),
                producto.getPrecioUnitario(),
                producto.getCodigoActividad(),
                catalogoProductoServicio.descripcionActividad(producto.getCodigoActividad()),
                producto.getCodigoProducto(),
                catalogoProductoServicio.descripcionProductoServicio(producto.getCodigoProducto()),
                producto.getUnidadMedida(),
                catalogoProductoServicio.descripcionUnidadMedida(producto.getUnidadMedida()));
    }
}
