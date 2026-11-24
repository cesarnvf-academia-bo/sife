package bo.org.citior.sife.servicio;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HexFormat;
import java.util.zip.GZIPOutputStream;
import org.springframework.stereotype.Service;

@Service
public class ComprimirFacturaServicio {

    public record ArchivoComprimido(byte[] gzip, String hashSha256, String base64) {
    }

    public ArchivoComprimido comprimir(byte[] xmlFirmado) {
        if (xmlFirmado == null || xmlFirmado.length == 0) {
            throw new IllegalArgumentException("El XML firmado es obligatorio.");
        }

        try {
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            try (GZIPOutputStream gzip = new GZIPOutputStream(salida)) {
                gzip.write(xmlFirmado);
            }
            byte[] comprimido = salida.toByteArray();
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String hash = HexFormat.of().formatHex(digest.digest(comprimido));
            String base64 = Base64.getEncoder().encodeToString(comprimido);
            return new ArchivoComprimido(comprimido, hash, base64);
        } catch (IOException | NoSuchAlgorithmException excepcion) {
            throw new IllegalStateException(
                    "No se pudo comprimir el XML firmado: " + excepcion.getMessage(), excepcion);
        }
    }
}
