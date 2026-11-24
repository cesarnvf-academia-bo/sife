package bo.org.citior.sife.controlador;

import bo.org.citior.sife.dominio.dto.CatalogoFacturaRespuesta;
import bo.org.citior.sife.dominio.dto.FacturaAnulacionSolicitud;
import bo.org.citior.sife.dominio.dto.FacturaRespuesta;
import bo.org.citior.sife.dominio.dto.FacturaSolicitud;
import bo.org.citior.sife.dominio.dto.PaginaRespuesta;
import bo.org.citior.sife.servicio.AnularFacturaServicio;
import bo.org.citior.sife.servicio.CatalogoFacturaServicio;
import bo.org.citior.sife.servicio.FacturaGestionServicio;
import bo.org.citior.sife.servicio.RevertirAnulacionFacturaServicio;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/facturas")
@RequiredArgsConstructor
@Tag(name = "Factura", description = "Gestión de facturas electrónicas de Compra Venta")
public class FacturaControlador {

    private static final Set<Integer> TAMANOS_PERMITIDOS = Set.of(5, 10, 15, 20);

    private final FacturaGestionServicio facturaGestionServicio;
    private final CatalogoFacturaServicio catalogoFacturaServicio;
    private final AnularFacturaServicio anularFacturaServicio;
    private final RevertirAnulacionFacturaServicio revertirAnulacionFacturaServicio;

    @GetMapping
    @Operation(summary = "Listar facturas", description = "Lista facturas con filtro y paginación.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Listado obtenido"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
    })
    public PaginaRespuesta<FacturaRespuesta> listar(
            @RequestParam(required = false) String busqueda,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "5") int size) {
        validarTamano(size);
        return facturaGestionServicio.listar(busqueda, page, size);
    }

    @GetMapping("/catalogos")
    @Operation(
            summary = "Listar catálogos de factura",
            description = "Lista sucursales, puntos de venta, clientes y catálogos SIAT.")
    @ApiResponse(responseCode = "200", description = "Catálogos obtenidos")
    public CatalogoFacturaRespuesta catalogos() {
        return catalogoFacturaServicio.catalogos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener factura", description = "Consulta una factura por su identificador.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Factura encontrada"),
            @ApiResponse(responseCode = "404", description = "Factura no encontrada")
    })
    public FacturaRespuesta obtenerPorId(@PathVariable Long id) {
        return facturaGestionServicio.obtenerPorId(id);
    }

    @PostMapping
    @Operation(
            summary = "Emitir factura",
            description = "Genera, firma, comprime, envía al SIAT y persiste una factura.")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Factura registrada"),
            @ApiResponse(responseCode = "400", description = "Datos de la factura inválidos"),
            @ApiResponse(responseCode = "409", description = "Configuración SIAT inválida"),
            @ApiResponse(responseCode = "502", description = "Servicio SIAT no disponible")
    })
    public ResponseEntity<FacturaRespuesta> emitir(
            @Valid @RequestBody FacturaSolicitud solicitud) {
        FacturaRespuesta respuesta = facturaGestionServicio.emitir(solicitud);
        return ResponseEntity.created(URI.create("/api/v1/facturas/" + respuesta.id()))
                .body(respuesta);
    }

    @PostMapping("/{id}/anular")
    @Operation(summary = "Anular factura", description = "Anula una factura emitida.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Factura anulada"),
            @ApiResponse(responseCode = "404", description = "Factura no encontrada"),
            @ApiResponse(responseCode = "409", description = "Estado de factura inválido"),
            @ApiResponse(responseCode = "502", description = "Servicio SIAT no disponible")
    })
    public FacturaRespuesta anular(
            @PathVariable Long id,
            @Valid @RequestBody FacturaAnulacionSolicitud solicitud) {
        return anularFacturaServicio.anular(id, solicitud);
    }

    @PostMapping("/{id}/revertir")
    @Operation(summary = "Revertir anulación", description = "Revierte la anulación de una factura.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Anulación revertida"),
            @ApiResponse(responseCode = "404", description = "Factura no encontrada"),
            @ApiResponse(responseCode = "409", description = "Estado de factura inválido"),
            @ApiResponse(responseCode = "502", description = "Servicio SIAT no disponible")
    })
    public FacturaRespuesta revertir(@PathVariable Long id) {
        return revertirAnulacionFacturaServicio.revertir(id);
    }

    @GetMapping("/{id}/xml")
    @Operation(summary = "Obtener XML firmado", description = "Devuelve el XML firmado de una factura.")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "XML obtenido"),
            @ApiResponse(responseCode = "400", description = "Factura sin XML firmado"),
            @ApiResponse(responseCode = "404", description = "Factura no encontrada")
    })
    public ResponseEntity<byte[]> verXml(@PathVariable Long id) {
        byte[] xml = facturaGestionServicio.obtenerXml(id);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_XML)
                .body(xml);
    }

    private void validarTamano(int size) {
        if (!TAMANOS_PERMITIDOS.contains(size)) {
            throw new IllegalArgumentException("El tamaño debe ser 5, 10, 15 o 20.");
        }
    }
}
