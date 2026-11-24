package bo.org.citior.sife.controlador;

import bo.org.citior.sife.dominio.dto.ClienteRespuesta;
import bo.org.citior.sife.dominio.dto.ClienteSolicitud;
import bo.org.citior.sife.dominio.dto.PaginaRespuesta;
import bo.org.citior.sife.dominio.dto.TipoDocumentoIdentidadRespuesta;
import bo.org.citior.sife.servicio.ClienteServicio;
import bo.org.citior.sife.servicio.TipoDocumentoIdentidadServicio;
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
@RequestMapping("/api/v1/clientes")
@RequiredArgsConstructor
@Tag(name = "Cliente", description = "Gestión de clientes")
public class ClienteControlador {

    private static final Set<Integer> TAMANOS_PERMITIDOS = Set.of(5, 10, 15, 20);

    private final ClienteServicio clienteServicio;
    private final TipoDocumentoIdentidadServicio tipoDocumentoIdentidadServicio;

    @GetMapping
    @Operation(summary = "Listar clientes", description = "Lista clientes con filtro y paginación.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
    })
    public PaginaRespuesta<ClienteRespuesta> listar(
            @RequestParam(required = false) String busqueda,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        validarTamano(size);
        return clienteServicio.listar(busqueda, page, size);
    }

    @GetMapping("/tipos-documento-identidad")
    @Operation(
            summary = "Listar tipos de documento de identidad",
            description = "Lista los tipos disponibles desde el catálogo sincronizado.")
    @ApiResponse(responseCode = "200", description = "Catálogo obtenido")
    public List<TipoDocumentoIdentidadRespuesta> listarTiposDocumentoIdentidad() {
        return tipoDocumentoIdentidadServicio.listar();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente", description = "Consulta un cliente por su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ClienteRespuesta obtenerPorId(@PathVariable Long id) {
        return clienteServicio.obtenerPorId(id);
    }

    @PostMapping
    @Operation(summary = "Adicionar cliente", description = "Crea un nuevo cliente.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente creado"),
            @ApiResponse(responseCode = "400", description = "Datos del cliente inválidos"),
            @ApiResponse(responseCode = "409", description = "Código interno duplicado")
    })
    public ResponseEntity<ClienteRespuesta> adicionar(
            @Valid @RequestBody ClienteSolicitud solicitud) {
        ClienteRespuesta respuesta = clienteServicio.adicionar(solicitud);
        return ResponseEntity.created(URI.create("/api/v1/clientes/" + respuesta.id()))
                .body(respuesta);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar cliente", description = "Actualiza un cliente existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente actualizado"),
            @ApiResponse(responseCode = "400", description = "Datos del cliente inválidos"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado"),
            @ApiResponse(responseCode = "409", description = "Código interno duplicado")
    })
    public ClienteRespuesta modificar(
            @PathVariable Long id,
            @Valid @RequestBody ClienteSolicitud solicitud) {
        return clienteServicio.modificar(id, solicitud);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Borrar cliente", description = "Elimina un cliente existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente borrado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        clienteServicio.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    private void validarTamano(int size) {
        if (!TAMANOS_PERMITIDOS.contains(size)) {
            throw new IllegalArgumentException("El tamaño debe ser 5, 10, 15 o 20.");
        }
    }
}
