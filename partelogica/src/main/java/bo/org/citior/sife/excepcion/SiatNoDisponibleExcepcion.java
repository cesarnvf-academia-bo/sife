package bo.org.citior.sife.excepcion;

public class SiatNoDisponibleExcepcion extends RuntimeException {

    private final String servicio;

    public SiatNoDisponibleExcepcion(String servicio, String detalle) {
        super("No se pudo completar la operación en el servicio "
                + servicio + ": " + detalle);
        this.servicio = servicio;
    }

    public String getServicio() {
        return servicio;
    }
}
