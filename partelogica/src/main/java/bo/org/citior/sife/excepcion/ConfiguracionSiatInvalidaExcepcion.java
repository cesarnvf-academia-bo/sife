package bo.org.citior.sife.excepcion;

public class ConfiguracionSiatInvalidaExcepcion extends RuntimeException {

    private final Long empresaId;
    private final String campo;

    public ConfiguracionSiatInvalidaExcepcion(Long empresaId, String campo) {
        super("La empresa " + empresaId
                + " tiene un valor inválido o ausente en '" + campo + "'.");
        this.empresaId = empresaId;
        this.campo = campo;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public String getCampo() {
        return campo;
    }
}
