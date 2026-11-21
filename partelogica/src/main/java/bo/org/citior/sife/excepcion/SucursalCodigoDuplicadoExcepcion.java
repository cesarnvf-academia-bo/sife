package bo.org.citior.sife.excepcion;

public class SucursalCodigoDuplicadoExcepcion extends RuntimeException {

    private final String codigo;

    public SucursalCodigoDuplicadoExcepcion(String codigo) {
        super("Ya existe una sucursal con el código " + codigo + ".");
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
