package bo.org.citior.sife.controlador;

import bo.org.citior.sife.dominio.dto.RespuestaVerificacionComunicacion;
import bo.org.citior.sife.servicio.VerificarComunicacionServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/empresas/{id}/verificar-comunicacion")
@RequiredArgsConstructor
@Tag(name = "Verificación SIAT", description = "Verificación de comunicación con los servicios SIAT")
public class VerificarComunicacionControlador {

    private final VerificarComunicacionServicio verificarComunicacionServicio;

    @GetMapping
    @Operation(
            summary = "Verificar comunicación",
            description = "Verifica la comunicación con los cuatro servicios SIAT usando el token de la empresa.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Verificación realizada"),
            @ApiResponse(responseCode = "404", description = "Empresa no encontrada"),
            @ApiResponse(responseCode = "409", description = "Token SIAT no configurado")
    })
    public ResponseEntity<RespuestaVerificacionComunicacion> verificar(@PathVariable Long id) {
        return ResponseEntity.ok(verificarComunicacionServicio.verificar(id));
    }
}
