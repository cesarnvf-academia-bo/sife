package bo.org.citior.sife.dominio.dto;

public record ClienteRespuesta(
        Long id,
        String codigoInterno,
        String cliente,
        String dit,
        Integer tipo,
        String tipoDescripcion,
        String correo) {
}
