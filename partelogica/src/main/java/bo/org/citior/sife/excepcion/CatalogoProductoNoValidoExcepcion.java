package bo.org.citior.sife.excepcion;

public class CatalogoProductoNoValidoExcepcion extends RuntimeException {

    private final String campo;
    private final Object valor;

    public CatalogoProductoNoValidoExcepcion(String campo, Object valor) {
        super("El catálogo " + campo + " con valor " + valor + " no es válido.");
        this.campo = campo;
        this.valor = valor;
    }

    public String getCampo() {
        return campo;
    }

    public Object getValor() {
        return valor;
    }
}
