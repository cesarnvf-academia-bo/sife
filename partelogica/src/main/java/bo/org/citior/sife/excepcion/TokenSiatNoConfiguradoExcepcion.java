package bo.org.citior.sife.excepcion;

public class TokenSiatNoConfiguradoExcepcion extends RuntimeException {

    private final Long empresaId;

    public TokenSiatNoConfiguradoExcepcion(Long empresaId) {
        super("La empresa " + empresaId + " no tiene token SIAT configurado.");
        this.empresaId = empresaId;
    }

    public Long getEmpresaId() {
        return empresaId;
    }
}
