package bo.org.citior.sife.dominio.dto;

import java.util.List;

public record CatalogoRespuesta(
        String funcion,
        String etiqueta,
        Long ejecucionId,
        String ejecutadoEn,
        List<String> columnas,
        List<CatalogoFila> filas) {
}
