package bo.org.citior.sife.dominio.dto;

import java.util.List;

public record RespuestaSiat(
        String codigoDescripcion,
        Integer codigoEstado,
        String codigoRecepcion,
        Boolean transaccion,
        List<MensajeSiat> mensajes) {
}
