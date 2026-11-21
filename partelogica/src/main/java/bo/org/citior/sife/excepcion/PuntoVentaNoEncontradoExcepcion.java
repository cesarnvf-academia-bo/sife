package bo.org.citior.sife.excepcion;

public class PuntoVentaNoEncontradoExcepcion extends RuntimeException {

    private final Long id;

    public PuntoVentaNoEncontradoExcepcion(Long id) {
        super("No se encontró el punto de venta con identificador " + id + ".");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
