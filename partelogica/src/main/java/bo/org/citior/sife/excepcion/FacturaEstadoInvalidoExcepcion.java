package bo.org.citior.sife.excepcion;

public class FacturaEstadoInvalidoExcepcion extends RuntimeException {

    private final Long id;
    private final String estadoEsperado;
    private final String estadoActual;

    public FacturaEstadoInvalidoExcepcion(Long id, String estadoEsperado, String estadoActual) {
        super("La factura con id " + id + " tiene estado " + estadoActual
                + " y se esperaba " + estadoEsperado);
        this.id = id;
        this.estadoEsperado = estadoEsperado;
        this.estadoActual = estadoActual;
    }

    public Long getId() {
        return id;
    }

    public String getEstadoEsperado() {
        return estadoEsperado;
    }

    public String getEstadoActual() {
        return estadoActual;
    }
}
