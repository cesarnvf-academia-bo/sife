package bo.org.citior.sife.servicio;

import java.math.BigInteger;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import org.springframework.stereotype.Service;

@Service
public class GenerarCufServicio {

    private static final int CODIGO_DOCUMENTO_SECTOR = 1;
    private static final ZoneId ZONA_SIAT = ZoneId.of("America/La_Paz");
    private static final DateTimeFormatter FECHA_FORMAT =
            DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS", Locale.ROOT).withZone(ZONA_SIAT);

    public String generar(
            long nitEmisor,
            Instant fechaEmision,
            int codigoSucursal,
            int codigoModalidad,
            int codigoEmision,
            int tipoFacturaDocumento,
            long numeroFactura,
            int codigoPuntoVenta,
            String codigoControl) {
        validarRango(nitEmisor, 1, 9999999999999L, "nitEmisor");
        if (fechaEmision == null) {
            throw new IllegalArgumentException("fechaEmision es obligatoria.");
        }
        validarRango(codigoSucursal, 0, 9999, "codigoSucursal");
        validarRango(codigoModalidad, 1, 3, "codigoModalidad");
        validarRango(codigoEmision, 1, 3, "codigoEmision");
        validarRango(tipoFacturaDocumento, 1, 3, "tipoFacturaDocumento");
        validarRango(numeroFactura, 1, 9999999999L, "numeroFactura");
        validarRango(codigoPuntoVenta, 0, 9999, "codigoPuntoVenta");
        if (codigoControl == null || codigoControl.isBlank()) {
            throw new IllegalArgumentException("codigoControl es obligatorio.");
        }

        long fechaHora = Long.parseLong(FECHA_FORMAT.format(fechaEmision));
        String cadena53 = String.format(
                Locale.ROOT,
                "%013d%017d%04d%01d%01d%01d%02d%010d%04d",
                nitEmisor,
                fechaHora,
                codigoSucursal,
                codigoModalidad,
                codigoEmision,
                tipoFacturaDocumento,
                CODIGO_DOCUMENTO_SECTOR,
                numeroFactura,
                codigoPuntoVenta);

        if (cadena53.length() != 53 || !cadena53.chars().allMatch(this::esDigito)) {
            throw new IllegalArgumentException("La cadena base del CUF debe tener 53 dígitos.");
        }

        String cadena54 = cadena53 + calcularModulo11(cadena53);
        String base16 = new BigInteger(cadena54).toString(16).toUpperCase(Locale.ROOT);
        return base16 + codigoControl.trim();
    }

    private void validarRango(long valor, long minimo, long maximo, String campo) {
        if (valor < minimo || valor > maximo) {
            throw new IllegalArgumentException(
                    campo + " debe estar entre " + minimo + " y " + maximo + ".");
        }
    }

    private boolean esDigito(int caracter) {
        return caracter >= '0' && caracter <= '9';
    }

    private int calcularModulo11(String cadena) {
        int suma = 0;
        int peso = 2;
        for (int indice = cadena.length() - 1; indice >= 0; indice--) {
            suma += (cadena.charAt(indice) - '0') * peso;
            peso = peso == 9 ? 2 : peso + 1;
        }
        int digito = suma % 11;
        return digito == 10 ? 1 : digito;
    }
}
