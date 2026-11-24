package bo.org.citior.sife.dominio.dto;

import java.math.BigDecimal;
import java.util.List;

public record FacturaRespuesta(
        Long id,
        long numeroFactura,
        String cuf,
        String estado,
        String clienteCodigoInterno,
        String clienteNombreRazonSocial,
        String fechaEmision,
        BigDecimal montoTotal,
        Integer respuestaCodigoEstado,
        String respuestaCodigoDescripcion,
        String respuestaCodigoRecepcion,
        Boolean respuestaTransaccion,
        List<MensajeSiat> respuestaMensajes,
        List<FacturaDetalleRespuesta> detalle) {
}
