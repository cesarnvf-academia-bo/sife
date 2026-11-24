package bo.org.citior.sife.dominio.dto;

public record MensajeSiat(
        Integer codigo,
        String descripcion,
        Boolean advertencia,
        Integer numeroArchivo,
        Integer numeroDetalle) {
}
