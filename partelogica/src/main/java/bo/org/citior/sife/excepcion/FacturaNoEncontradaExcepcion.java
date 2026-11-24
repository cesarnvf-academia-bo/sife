package bo.org.citior.sife.excepcion;

public class FacturaNoEncontradaExcepcion extends RuntimeException {

    private final Long id;

    public FacturaNoEncontradaExcepcion(Long id) {
        super("No se encontró la factura con id " + id);
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
