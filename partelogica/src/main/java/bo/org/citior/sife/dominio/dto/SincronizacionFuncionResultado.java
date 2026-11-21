package bo.org.citior.sife.dominio.dto;

import java.util.List;

public record SincronizacionFuncionResultado(
        String funcion,
        String etiqueta,
        String estado,
        int registros,
        String error,
        List<MensajeRespuesta> mensajes) {

    public record MensajeRespuesta(Integer codigo, String descripcion) {
    }
}
