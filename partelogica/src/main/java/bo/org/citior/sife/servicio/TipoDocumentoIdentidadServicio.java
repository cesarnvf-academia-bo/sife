package bo.org.citior.sife.servicio;

import bo.org.citior.sife.dominio.dto.TipoDocumentoIdentidadRespuesta;
import bo.org.citior.sife.mapper.SincronizacionMapper;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TipoDocumentoIdentidadServicio {

    private static final List<TipoDocumentoIdentidadRespuesta> TIPOS_PREDETERMINADOS = List.of(
            new TipoDocumentoIdentidadRespuesta(1, "CI - CEDULA DE IDENTIDAD"),
            new TipoDocumentoIdentidadRespuesta(2, "CEX - CEDULA DE IDENTIDAD DE EXTRANJERO"),
            new TipoDocumentoIdentidadRespuesta(3, "PAS - PASAPORTE"),
            new TipoDocumentoIdentidadRespuesta(4, "OD - OTRO DOCUMENTO DE IDENTIDAD"),
            new TipoDocumentoIdentidadRespuesta(
                    5, "NIT - NÚMERO DE IDENTIFICACIÓN TRIBUTARIA"));

    private final SincronizacionMapper sincronizacionMapper;

    @Transactional(readOnly = true)
    public List<TipoDocumentoIdentidadRespuesta> listar() {
        Map<Integer, String> descripciones = new LinkedHashMap<>();
        for (TipoDocumentoIdentidadRespuesta tipo : TIPOS_PREDETERMINADOS) {
            descripciones.put(tipo.codigo(), tipo.descripcion());
        }

        List<TipoDocumentoIdentidadRespuesta> sincronizados =
                sincronizacionMapper.listarTiposDocumentoIdentidad();
        if (sincronizados != null) {
            for (TipoDocumentoIdentidadRespuesta tipo : sincronizados) {
                if (tipo.codigo() != null
                        && descripciones.containsKey(tipo.codigo())
                        && tipo.descripcion() != null
                        && !tipo.descripcion().isBlank()) {
                    descripciones.put(tipo.codigo(), tipo.descripcion());
                }
            }
        }

        return descripciones.entrySet().stream()
                .map(entrada -> new TipoDocumentoIdentidadRespuesta(
                        entrada.getKey(), entrada.getValue()))
                .toList();
    }

    @Transactional(readOnly = true)
    public String descripcion(Integer codigo) {
        return listar().stream()
                .filter(tipo -> tipo.codigo().equals(codigo))
                .map(TipoDocumentoIdentidadRespuesta::descripcion)
                .findFirst()
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public boolean existe(Integer codigo) {
        return codigo != null && listar().stream()
                .anyMatch(tipo -> tipo.codigo().equals(codigo));
    }
}
