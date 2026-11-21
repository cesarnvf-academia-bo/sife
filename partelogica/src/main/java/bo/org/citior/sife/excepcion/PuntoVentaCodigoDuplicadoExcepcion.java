package bo.org.citior.sife.excepcion;

public class PuntoVentaCodigoDuplicadoExcepcion extends RuntimeException {

    private final String codigo;

    public PuntoVentaCodigoDuplicadoExcepcion(String codigo) {
        super("Ya existe un punto de venta con el código " + codigo + " en la sucursal.");
        this.codigo = codigo;
    }

    public String getCodigo() {
        return codigo;
    }
}
