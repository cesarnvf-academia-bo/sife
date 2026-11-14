package bo.org.citior.sife.controlador;

import bo.org.citior.sife.dominio.RespuestaPing;
import bo.org.citior.sife.servicio.PingServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/ping")
@Tag(name = "Ping", description = "Verificación de disponibilidad del servicio")
public class PingControlador {

    private final PingServicio pingServicio;

    public PingControlador(PingServicio pingServicio) {
        this.pingServicio = pingServicio;
    }

    @GetMapping
    @Operation(summary = "Verificar disponibilidad", description = "Confirma que el servicio se encuentra activo.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Servicio activo")
    })
    public ResponseEntity<RespuestaPing> responderPing() {
        return ResponseEntity.ok(pingServicio.obtenerRespuesta());
    }
}
