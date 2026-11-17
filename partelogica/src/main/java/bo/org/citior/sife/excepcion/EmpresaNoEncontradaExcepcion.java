package bo.org.citior.sife.excepcion;

public class EmpresaNoEncontradaExcepcion extends RuntimeException {

    private final Long id;

    public EmpresaNoEncontradaExcepcion(Long id) {
        super("No se encontró la empresa con identificador " + id + ".");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
