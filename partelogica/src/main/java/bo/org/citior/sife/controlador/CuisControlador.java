package bo.org.citior.sife.controlador;

import bo.org.citior.sife.dominio.dto.CuisRespuesta;
import bo.org.citior.sife.dominio.dto.CuisSolicitud;
import bo.org.citior.sife.servicio.ObtenerCuisServicio;
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
@RequestMapping("/api/v1/empresas/{id}/cuis")
@RequiredArgsConstructor
@Tag(name = "CUIS", description = "Obtención del CUIS desde el SIAT")
public class CuisControlador {

    private final ObtenerCuisServicio obtenerCuisServicio;

    @PostMapping
    @Operation(
            summary = "Obtener CUIS",
            description = "Obtiene el CUIS para la sucursal 0 y el punto de venta indicado.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Respuesta del SIAT"),
            @ApiResponse(responseCode = "400", description = "Punto de venta inválido"),
            @ApiResponse(responseCode = "404", description = "Empresa no encontrada"),
            @ApiResponse(responseCode = "409", description = "Token o configuración SIAT inválida"),
            @ApiResponse(responseCode = "502", description = "Servicio SIAT no disponible")
    })
    public ResponseEntity<CuisRespuesta> obtener(
            @PathVariable Long id,
            @Valid @RequestBody CuisSolicitud solicitud) {

        return ResponseEntity.ok(obtenerCuisServicio.obtener(id, solicitud));
    }
}
