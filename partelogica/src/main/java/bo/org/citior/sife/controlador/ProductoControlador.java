package bo.org.citior.sife.controlador;

import bo.org.citior.sife.dominio.dto.ActividadOpcion;
import bo.org.citior.sife.dominio.dto.PaginaRespuesta;
import bo.org.citior.sife.dominio.dto.ProductoRespuesta;
import bo.org.citior.sife.dominio.dto.ProductoServicioOpcion;
import bo.org.citior.sife.dominio.dto.ProductoSolicitud;
import bo.org.citior.sife.dominio.dto.UnidadMedidaOpcion;
import bo.org.citior.sife.servicio.CatalogoProductoServicio;
import bo.org.citior.sife.servicio.ProductoGestionServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/productos")
@RequiredArgsConstructor
@Tag(name = "Producto", description = "Gestión de productos y catálogos SIAT")
public class ProductoControlador {

    private static final Set<Integer> TAMANOS_PERMITIDOS = Set.of(5, 10, 15, 20);

    private final ProductoGestionServicio productoGestionServicio;
    private final CatalogoProductoServicio catalogoProductoServicio;

    @GetMapping
    @Operation(summary = "Listar productos", description = "Lista productos con filtro y paginación.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
    })
    public PaginaRespuesta<ProductoRespuesta> listar(
            @RequestParam(required = false) String busqueda,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        validarTamano(size);
        return productoGestionServicio.listar(busqueda, page, size);
    }

    @GetMapping("/actividades")
    @Operation(
            summary = "Listar actividades",
            description = "Lista las actividades de la última sincronización.")
    @ApiResponse(responseCode = "200", description = "Catálogo obtenido")
    public List<ActividadOpcion> listarActividades() {
        return catalogoProductoServicio.listarActividades();
    }

    @GetMapping("/productos-servicio")
    @Operation(
            summary = "Listar productos/servicios",
            description = "Lista los productos/servicios de la última sincronización.")
    @ApiResponse(responseCode = "200", description = "Catálogo obtenido")
    public List<ProductoServicioOpcion> listarProductosServicio() {
        return catalogoProductoServicio.listarProductosServicio();
    }

    @GetMapping("/unidades-medida")
    @Operation(
            summary = "Listar unidades de medida",
            description = "Lista las unidades de medida de la última sincronización.")
    @ApiResponse(responseCode = "200", description = "Catálogo obtenido")
    public List<UnidadMedidaOpcion> listarUnidadesMedida() {
        return catalogoProductoServicio.listarUnidadesMedida();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto", description = "Consulta un producto por su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto encontrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ProductoRespuesta obtenerPorId(@PathVariable Long id) {
        return productoGestionServicio.obtenerPorId(id);
    }

    @PostMapping
    @Operation(summary = "Adicionar producto", description = "Crea un nuevo producto.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Producto creado"),
            @ApiResponse(responseCode = "400", description = "Datos del producto inválidos"),
            @ApiResponse(responseCode = "409", description = "Código interno duplicado")
    })
    public ResponseEntity<ProductoRespuesta> adicionar(
            @Valid @RequestBody ProductoSolicitud solicitud) {
        ProductoRespuesta respuesta = productoGestionServicio.adicionar(solicitud);
        return ResponseEntity.created(URI.create("/api/v1/productos/" + respuesta.id()))
                .body(respuesta);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar producto", description = "Actualiza un producto existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Producto actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos del producto inválidos"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado"),
            @ApiResponse(responseCode = "409", description = "Código interno duplicado")
    })
    public ProductoRespuesta modificar(
            @PathVariable Long id,
            @Valid @RequestBody ProductoSolicitud solicitud) {
        return productoGestionServicio.modificar(id, solicitud);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Borrar producto", description = "Elimina un producto existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Producto borrado"),
            @ApiResponse(responseCode = "404", description = "Producto no encontrado")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        productoGestionServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private void validarTamano(int size) {
        if (!TAMANOS_PERMITIDOS.contains(size)) {
            throw new IllegalArgumentException("El tamaño debe ser 5, 10, 15 o 20.");
        }
    }
}
