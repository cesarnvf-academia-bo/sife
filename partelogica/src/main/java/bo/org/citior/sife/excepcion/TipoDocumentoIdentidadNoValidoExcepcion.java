package bo.org.citior.sife.excepcion;

public class TipoDocumentoIdentidadNoValidoExcepcion extends RuntimeException {

    private final Integer codigo;

    public TipoDocumentoIdentidadNoValidoExcepcion(Integer codigo) {
        super("El tipo de documento de identidad " + codigo + " no es válido.");
        this.codigo = codigo;
    }

    public Integer getCodigo() {
        return codigo;
    }
}
