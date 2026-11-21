package bo.org.citior.sife.dominio.dto;

import bo.org.citior.sife.dominio.enumeracion.TipoCatalogo;

public record SincronizacionFuncionRespuesta(
        String nombre,
        String etiqueta,
        TipoCatalogo tipo) {
}
