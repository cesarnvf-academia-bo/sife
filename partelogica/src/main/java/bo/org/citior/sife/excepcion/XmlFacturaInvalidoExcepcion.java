package bo.org.citior.sife.excepcion;

public class XmlFacturaInvalidoExcepcion extends RuntimeException {

    private final String detalle;

    public XmlFacturaInvalidoExcepcion(String detalle) {
        super(detalle);
        this.detalle = detalle;
    }

    public String getDetalle() {
        return detalle;
    }
}
