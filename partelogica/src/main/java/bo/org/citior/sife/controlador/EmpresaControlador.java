package bo.org.citior.sife.controlador;

import bo.org.citior.sife.dominio.dto.EmpresaRespuesta;
import bo.org.citior.sife.dominio.dto.EmpresaSolicitud;
import bo.org.citior.sife.servicio.EmpresaServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/empresas")
@RequiredArgsConstructor
@Tag(name = "Empresa", description = "Gestión de empresas")
public class EmpresaControlador {

    private final EmpresaServicio empresaServicio;

    @PostMapping
    @Operation(summary = "Adicionar empresa", description = "Crea una nueva empresa.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Empresa creada"),
            @ApiResponse(responseCode = "400", description = "Datos de la empresa inválidos")
    })
    public ResponseEntity<EmpresaRespuesta> adicionar(
            @Valid @RequestBody EmpresaSolicitud solicitud) {
        EmpresaRespuesta respuesta = empresaServicio.adicionar(solicitud);
        return ResponseEntity.created(URI.create("/api/v1/empresas/" + respuesta.id()))
                .body(respuesta);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Modificar empresa", description = "Actualiza una empresa existente.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Empresa actualizada"),
            @ApiResponse(responseCode = "400", description = "Datos de la empresa inválidos"),
            @ApiResponse(responseCode = "404", description = "Empresa no encontrada")
    })
    public ResponseEntity<EmpresaRespuesta> modificar(
            @PathVariable Long id,
            @Valid @RequestBody EmpresaSolicitud solicitud) {
        return ResponseEntity.ok(empresaServicio.modificar(id, solicitud));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener empresa", description = "Consulta una empresa por su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Empresa encontrada"),
            @ApiResponse(responseCode = "404", description = "Empresa no encontrada")
    })
    public ResponseEntity<EmpresaRespuesta> obtenerPorId(@PathVariable Long id) {
        return ResponseEntity.ok(empresaServicio.obtenerPorId(id));
    }
}
