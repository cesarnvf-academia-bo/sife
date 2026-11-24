package bo.org.citior.sife.excepcion;

public class ClienteCodigoInternoDuplicadoExcepcion extends RuntimeException {

    private final String codigoInterno;

    public ClienteCodigoInternoDuplicadoExcepcion(String codigoInterno) {
        super("El código interno de cliente " + codigoInterno + " ya está registrado.");
        this.codigoInterno = codigoInterno;
    }

    public String getCodigoInterno() {
        return codigoInterno;
    }
}
