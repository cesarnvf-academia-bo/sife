package bo.org.citior.sife.controlador;

import bo.org.citior.sife.dominio.dto.PaginaRespuesta;
import bo.org.citior.sife.dominio.dto.SucursalRespuesta;
import bo.org.citior.sife.dominio.dto.SucursalSolicitud;
import bo.org.citior.sife.servicio.SucursalServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
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
@RequestMapping("/api/v1/sucursales")
@RequiredArgsConstructor
@Tag(name = "Sucursal", description = "Gestión de sucursales")
public class SucursalControlador {

    private static final Set<Integer> TAMANOS_PERMITIDOS = Set.of(5, 10, 15, 20);

    private final SucursalServicio sucursalServicio;

    @GetMapping
    @Operation(summary = "Listar sucursales", description = "Lista sucursales con filtro y paginación.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
    })
    public PaginaRespuesta<SucursalRespuesta> listar(
            @RequestParam(required = false) String busqueda,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        validarTamano(size);
        return sucursalServicio.listar(busqueda, page, size);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener sucursal", description = "Consulta una sucursal por su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sucursal encontrada"),
            @ApiResponse(responseCode = "404", description = "Sucursal no encontrada")
    })
    public SucursalRespuesta obtenerPorId(@PathVariable Long id) {
        return sucursalServicio.obtenerPorId(id);
    }

    @PostMapping
    @Operation(summary = "Adicionar sucursal", description = "Crea una nueva sucursal.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Sucursal creada"),
            @ApiResponse(responseCode = "400", description = "Datos de la sucursal inválidos"),
            @ApiResponse(responseCode = "409", description = "Código de sucursal duplicado")
    })
    public ResponseEntity<SucursalRespuesta> adicionar(
            @Valid @RequestBody SucursalSolicitud solicitud) {
        SucursalRespuesta respuesta = sucursalServicio.adicionar(solicitud);
        return ResponseEntity.created(URI.create("/api/v1/sucursales/" + respuesta.id()))
                .body(respuesta);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar sucursal", description = "Actualiza una sucursal existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Sucursal actualizada"),
            @ApiResponse(responseCode = "400", description = "Datos de la sucursal inválidos"),
            @ApiResponse(responseCode = "404", description = "Sucursal no encontrada"),
            @ApiResponse(responseCode = "409", description = "Código de sucursal duplicado")
    })
    public SucursalRespuesta modificar(
            @PathVariable Long id,
            @Valid @RequestBody SucursalSolicitud solicitud) {
        return sucursalServicio.modificar(id, solicitud);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Borrar sucursal", description = "Elimina una sucursal existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Sucursal borrada"),
            @ApiResponse(responseCode = "404", description = "Sucursal no encontrada")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        sucursalServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private void validarTamano(int size) {
        if (!TAMANOS_PERMITIDOS.contains(size)) {
            throw new IllegalArgumentException("El tamaño debe ser 5, 10, 15 o 20.");
        }
    }
}
