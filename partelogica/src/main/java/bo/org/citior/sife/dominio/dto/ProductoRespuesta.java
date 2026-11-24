package bo.org.citior.sife.dominio.dto;

import java.math.BigDecimal;

public record ProductoRespuesta(
        Long id,
        String codigoInterno,
        String producto,
        BigDecimal precioUnitario,
        String codigoActividad,
        String actividadDescripcion,
        Long codigoProducto,
        String productoDescripcion,
        Integer unidadMedida,
        String unidadMedidaDescripcion) {
}
