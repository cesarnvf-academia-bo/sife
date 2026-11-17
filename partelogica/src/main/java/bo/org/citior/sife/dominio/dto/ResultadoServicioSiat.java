package bo.org.citior.sife.dominio.dto;

public record ResultadoServicioSiat(
        String servicio,
        boolean comunicacion,
        Integer codigo,
        String mensaje) {
}
