package bo.org.citior.sife.dominio.dto;

import java.util.List;

public record CufdRespuesta(
        Long empresaId,
        String nit,
        int codigoSucursal,
        int codigoPuntoVenta,
        String cufd,
        String codigoControl,
        String direccion,
        String fechaVigencia,
        boolean transaccion,
        List<MensajeCufd> mensajes) {

    public record MensajeCufd(Integer codigo, String descripcion) {
    }
}
