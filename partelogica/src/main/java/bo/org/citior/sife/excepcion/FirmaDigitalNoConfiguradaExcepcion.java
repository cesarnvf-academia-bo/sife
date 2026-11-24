package bo.org.citior.sife.excepcion;

public class FirmaDigitalNoConfiguradaExcepcion extends RuntimeException {

    private final String detalle;

    public FirmaDigitalNoConfiguradaExcepcion(String detalle) {
        super(detalle);
        this.detalle = detalle;
    }

    public String getDetalle() {
        return detalle;
    }
}
