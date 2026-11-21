package bo.org.citior.sife.servicio;

import bo.org.citior.sife.dominio.Cuis;
import bo.org.citior.sife.dominio.PuntoVenta;
import bo.org.citior.sife.dominio.dto.CuisRespuesta;
import bo.org.citior.sife.mapper.CuisMapper;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class RegistroCuisServicio {

    private final CuisMapper cuisMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public Long registrar(CuisRespuesta respuesta, PuntoVenta puntoVenta) {
        Long empresaId = respuesta.empresaId() == null
                ? EmpresaServicio.EMPRESA_ID
                : respuesta.empresaId();

        Cuis cuis = Cuis.builder()
                .empresaId(empresaId)
                .sucursalId(puntoVenta.getSucursalId())
                .puntoventaId(puntoVenta.getId())
                .codigoSucursal(respuesta.codigoSucursal())
                .codigoPuntoVenta(respuesta.codigoPuntoVenta())
                .cuis(respuesta.cuis() == null ? "" : respuesta.cuis())
                .fechaVigencia(parsearFecha(respuesta.fechaVigencia()))
                .transaccion(respuesta.transaccion())
                .mensajes(serializar(respuesta.mensajes()))
                .build();

        cuisMapper.insert(cuis);
        cuisMapper.activarVigente(empresaId, puntoVenta.getId());
        return cuis.getId();
    }

    private Instant parsearFecha(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(valor.trim()).toInstant();
        } catch (DateTimeParseException excepcion) {
            return null;
        }
    }

    private String serializar(List<CuisRespuesta.MensajeCuis> mensajes) {
        try {
            return objectMapper.writeValueAsString(
                    mensajes == null ? List.of() : mensajes);
        } catch (JacksonException excepcion) {
            return "[]";
        }
    }
}
