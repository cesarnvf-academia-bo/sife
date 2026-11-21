package bo.org.citior.sife.dominio.dto;

public record PuntoVentaRespuesta(
        Long id,
        Long sucursalId,
        String codigo,
        String codigoSucursal,
        String puntoventa,
        int tipo,
        String tipoDescripcion,
        boolean estado) {
}
