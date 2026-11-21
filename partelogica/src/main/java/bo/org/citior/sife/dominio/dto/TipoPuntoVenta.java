package bo.org.citior.sife.dominio.dto;

import java.util.Arrays;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TipoPuntoVenta {

    COMISIONISTA(1, "Punto Venta Comisionista"),
    VENTANILLA_COBRANZA(2, "Punto Venta Ventanilla de Cobranza"),
    MOVILES(3, "Punto de Venta Móviles"),
    YPFB(4, "Punto de Venta YPFB (combustible a precio internacional)"),
    CAJEROS(5, "Punto de Venta Cajeros"),
    CONJUNTA(6, "Punto de Venta Conjunta");

    private final int codigo;
    private final String descripcion;

    public static TipoPuntoVenta desde(int codigo) {
        return Arrays.stream(values())
                .filter(tipo -> tipo.codigo == codigo)
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("El tipo debe estar entre 1 y 6."));
    }

    public static TipoPuntoVenta porDefecto() {
        return CAJEROS;
    }
}
