package bo.org.citior.sife.excepcion;

public class ProductoNoEncontradoExcepcion extends RuntimeException {

    private final Long id;

    public ProductoNoEncontradoExcepcion(Long id) {
        super("No se encontró el producto con identificador " + id + ".");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
