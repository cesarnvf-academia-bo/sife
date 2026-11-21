package bo.org.citior.sife.controlador;

import bo.org.citior.sife.dominio.dto.CatalogoRespuesta;
import bo.org.citior.sife.dominio.dto.CufdRespuesta;
import bo.org.citior.sife.dominio.dto.CuisRespuesta;
import bo.org.citior.sife.dominio.dto.PaginaRespuesta;
import bo.org.citior.sife.dominio.dto.PuntoVentaRespuesta;
import bo.org.citior.sife.dominio.dto.PuntoVentaSolicitud;
import bo.org.citior.sife.dominio.dto.SincronizacionFuncionRespuesta;
import bo.org.citior.sife.dominio.dto.SincronizacionResumen;
import bo.org.citior.sife.servicio.ObtenerCatalogoServicio;
import bo.org.citior.sife.servicio.PuntoVentaServicio;
import bo.org.citior.sife.servicio.SincronizarCatalogosServicio;
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
@RequestMapping("/api/v1/puntosventa")
@RequiredArgsConstructor
@Tag(name = "Punto Venta", description = "Gestión de puntos de venta y obtención de CUIS/CUFD")
public class PuntoVentaControlador {

    private static final Set<Integer> TAMANOS_PERMITIDOS = Set.of(5, 10, 15, 20);

    private final PuntoVentaServicio puntoVentaServicio;
    private final ObtenerCatalogoServicio obtenerCatalogoServicio;
    private final SincronizarCatalogosServicio sincronizarCatalogosServicio;

    @GetMapping
    @Operation(summary = "Listar puntos de venta", description = "Lista puntos de venta con filtro y paginación.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
    })
    public PaginaRespuesta<PuntoVentaRespuesta> listar(
            @RequestParam(required = false) String busqueda,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        validarTamano(size);
        return puntoVentaServicio.listar(busqueda, page, size);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener punto de venta", description = "Consulta un punto de venta por su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Punto de venta encontrado"),
            @ApiResponse(responseCode = "404", description = "Punto de venta no encontrado")
    })
    public PuntoVentaRespuesta obtenerPorId(@PathVariable Long id) {
        return puntoVentaServicio.obtenerPorId(id);
    }

    @PostMapping
    @Operation(summary = "Adicionar punto de venta", description = "Crea un nuevo punto de venta.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Punto de venta creado"),
            @ApiResponse(responseCode = "400", description = "Datos del punto de venta inválidos"),
            @ApiResponse(responseCode = "409", description = "Código de punto de venta duplicado")
    })
    public ResponseEntity<PuntoVentaRespuesta> adicionar(
            @Valid @RequestBody PuntoVentaSolicitud solicitud) {
        PuntoVentaRespuesta respuesta = puntoVentaServicio.adicionar(solicitud);
        return ResponseEntity.created(URI.create("/api/v1/puntosventa/" + respuesta.id()))
                .body(respuesta);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar punto de venta", description = "Actualiza un punto de venta existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Punto de venta actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos del punto de venta inválidos"),
            @ApiResponse(responseCode = "404", description = "Punto de venta no encontrado"),
            @ApiResponse(responseCode = "409", description = "Código de punto de venta duplicado")
    })
    public PuntoVentaRespuesta modificar(
            @PathVariable Long id,
            @Valid @RequestBody PuntoVentaSolicitud solicitud) {
        return puntoVentaServicio.modificar(id, solicitud);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Borrar punto de venta", description = "Elimina un punto de venta existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Punto de venta borrado"),
            @ApiResponse(responseCode = "404", description = "Punto de venta no encontrado")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        puntoVentaServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cuis")
    @Operation(summary = "Obtener CUIS", description = "Obtiene y registra el CUIS del punto de venta.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Respuesta del SIAT"),
            @ApiResponse(responseCode = "404", description = "Punto de venta no encontrado"),
            @ApiResponse(responseCode = "409", description = "Configuración SIAT inválida"),
            @ApiResponse(responseCode = "502", description = "Servicio SIAT no disponible")
    })
    public CuisRespuesta obtenerCuis(@PathVariable Long id) {
        return puntoVentaServicio.obtenerCuis(id);
    }

    @PostMapping("/{id}/cufd")
    @Operation(summary = "Obtener CUFD", description = "Obtiene y registra el CUFD del punto de venta.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Respuesta del SIAT"),
            @ApiResponse(responseCode = "404", description = "Punto de venta no encontrado"),
            @ApiResponse(responseCode = "409", description = "Configuración SIAT inválida"),
            @ApiResponse(responseCode = "502", description = "Servicio SIAT no disponible")
    })
    public CufdRespuesta obtenerCufd(@PathVariable Long id) {
        return puntoVentaServicio.obtenerCufd(id);
    }

    @GetMapping("/{id}/catalogos/funciones")
    @Operation(summary = "Listar funciones de sincronización", description = "Devuelve las funciones de catálogo disponibles.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Funciones obtenidas"),
            @ApiResponse(responseCode = "404", description = "Punto de venta no encontrado")
    })
    public List<SincronizacionFuncionRespuesta> listarFunciones(@PathVariable Long id) {
        puntoVentaServicio.obtenerModelo(id);
        return obtenerCatalogoServicio.listarFunciones();
    }

    @PostMapping("/{id}/catalogos/sincronizar")
    @Operation(summary = "Sincronizar catálogos", description = "Ejecuta las 18 funciones SOAP de sincronización.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sincronización finalizada"),
            @ApiResponse(responseCode = "404", description = "Punto de venta no encontrado"),
            @ApiResponse(responseCode = "409", description = "Configuración SIAT inválida"),
            @ApiResponse(responseCode = "502", description = "Servicio SIAT no disponible")
    })
    public SincronizacionResumen sincronizarCatalogos(@PathVariable Long id) {
        return sincronizarCatalogosServicio.sincronizar(id);
    }

    @GetMapping("/{id}/catalogos/{funcion}")
    @Operation(summary = "Mostrar catálogo", description = "Devuelve los datos de la última ejecución exitosa de una función.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Catálogo obtenido"),
            @ApiResponse(responseCode = "400", description = "Función no válida"),
            @ApiResponse(responseCode = "404", description = "Punto de venta no encontrado")
    })
    public CatalogoRespuesta obtenerCatalogo(
            @PathVariable Long id,
            @PathVariable String funcion) {
        return obtenerCatalogoServicio.obtener(id, funcion);
    }

    private void validarTamano(int size) {
        if (!TAMANOS_PERMITIDOS.contains(size)) {
            throw new IllegalArgumentException("El tamaño debe ser 5, 10, 15 o 20.");
        }
    }
}
