package bo.org.citior.sife.excepcion;

public class SucursalNoEncontradaExcepcion extends RuntimeException {

    private final Long id;

    public SucursalNoEncontradaExcepcion(Long id) {
        super("No se encontró la sucursal con identificador " + id + ".");
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
