package bo.org.citior.sife.mapeador;

import bo.org.citior.sife.dominio.Factura;
import bo.org.citior.sife.dominio.FacturaDetalle;
import bo.org.citior.sife.dominio.dto.FacturaDetalleRespuesta;
import bo.org.citior.sife.dominio.dto.FacturaRespuesta;
import bo.org.citior.sife.dominio.dto.MensajeSiat;
import java.time.format.DateTimeFormatter;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
public class FacturaMapeador {

    private final ObjectMapper objectMapper;

    public FacturaRespuesta aRespuesta(Factura factura, List<FacturaDetalle> detalles) {
        List<FacturaDetalleRespuesta> detalleRespuesta = detalles.stream()
                .map(this::aDetalleRespuesta)
                .toList();
        String fechaEmision = factura.getFechaEmision() == null
                ? null
                : DateTimeFormatter.ISO_INSTANT.format(factura.getFechaEmision());
        return new FacturaRespuesta(
                factura.getId(),
                factura.getNumeroFactura(),
                factura.getCuf(),
                factura.getEstado(),
                factura.getCodigoCliente(),
                factura.getNombreRazonSocial(),
                fechaEmision,
                factura.getMontoTotal(),
                factura.getRespuestaCodigoEstado(),
                factura.getRespuestaCodigoDescripcion(),
                factura.getRespuestaCodigoRecepcion(),
                factura.getRespuestaTransaccion(),
                deserializarMensajes(factura.getRespuestaMensajes()),
                detalleRespuesta);
    }

    public FacturaDetalleRespuesta aDetalleRespuesta(FacturaDetalle detalle) {
        return new FacturaDetalleRespuesta(
                detalle.getId(),
                detalle.getLinea(),
                detalle.getActividadEconomica(),
                detalle.getCodigoProductoSin(),
                detalle.getCodigoProducto(),
                detalle.getDescripcion(),
                detalle.getCantidad(),
                detalle.getUnidadMedida(),
                detalle.getPrecioUnitario(),
                detalle.getMontoDescuento(),
                detalle.getSubTotal(),
                detalle.getNumeroSerie(),
                detalle.getNumeroImei());
    }

    public String serializarMensajes(List<MensajeSiat> mensajes) {
        try {
            return objectMapper.writeValueAsString(mensajes == null ? List.of() : mensajes);
        } catch (JacksonException excepcion) {
            return "[]";
        }
    }

    public List<MensajeSiat> deserializarMensajes(String json) {
        if (json == null || json.isBlank()) {
            return List.of();
        }
        try {
            return objectMapper.readerForListOf(MensajeSiat.class).readValue(json);
        } catch (JacksonException excepcion) {
            return List.of();
        }
    }
}
