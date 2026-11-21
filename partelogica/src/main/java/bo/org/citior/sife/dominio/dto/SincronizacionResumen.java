package bo.org.citior.sife.dominio.dto;

import java.util.List;

public record SincronizacionResumen(
        Long puntoVentaId,
        String cuis,
        int total,
        int exitosas,
        int fallidas,
        List<SincronizacionFuncionResultado> resultados) {
}
