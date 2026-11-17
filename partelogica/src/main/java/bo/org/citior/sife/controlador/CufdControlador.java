package bo.org.citior.sife.controlador;

import bo.org.citior.sife.dominio.dto.CufdRespuesta;
import bo.org.citior.sife.dominio.dto.CufdSolicitud;
import bo.org.citior.sife.servicio.ObtenerCufdServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/empresas/{id}/cufd")
@RequiredArgsConstructor
@Tag(name = "CUFD", description = "Obtención del CUFD desde el SIAT")
public class CufdControlador {

    private final ObtenerCufdServicio obtenerCufdServicio;

    @PostMapping
    @Operation(
            summary = "Obtener CUFD",
            description = "Obtiene el CUFD para la sucursal 0 y el punto de venta indicado. "
                    + "Obtiene internamente el CUIS requerido.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Respuesta del SIAT"),
            @ApiResponse(responseCode = "400", description = "Punto de venta inválido"),
            @ApiResponse(responseCode = "404", description = "Empresa no encontrada"),
            @ApiResponse(responseCode = "409", description = "Token o configuración SIAT inválida"),
            @ApiResponse(responseCode = "502", description = "No se pudo obtener el CUIS o el CUFD")
    })
    public ResponseEntity<CufdRespuesta> obtener(
            @PathVariable Long id,
            @Valid @RequestBody CufdSolicitud solicitud) {

        return ResponseEntity.ok(obtenerCufdServicio.obtener(id, solicitud));
    }
}
