package bo.org.citior.sife.dominio.dto;

public record EmpresaRespuesta(
        Long id,
        String nit,
        String razonsocial,
        String codigoSistema,
        String modalidad,
        String ambiente,
        String rutaCodigo,
        String rutaSincronizacion,
        String rutaOperacion,
        String rutaCompraventa,
        String rutaFirmaDigital,
        boolean tieneToken,
        boolean tieneClaveFirmaDigital
) {
}
