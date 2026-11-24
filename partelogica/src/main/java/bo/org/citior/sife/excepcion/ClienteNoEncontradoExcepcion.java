package bo.org.citior.sife.excepcion;

public class ClienteNoEncontradoExcepcion extends RuntimeException {

    private final Long id;

    public ClienteNoEncontradoExcepcion(Long id) {
        super("No se encontró el cliente con identificador " + id + ".");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
