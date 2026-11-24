package bo.org.citior.sife.excepcion;

public class ProductoCodigoInternoDuplicadoExcepcion extends RuntimeException {

    private final String codigoInterno;

    public ProductoCodigoInternoDuplicadoExcepcion(String codigoInterno) {
        super("El código interno de producto " + codigoInterno + " ya está registrado.");
        this.codigoInterno = codigoInterno;
    }

    public String getCodigoInterno() {
        return codigoInterno;
    }
}
