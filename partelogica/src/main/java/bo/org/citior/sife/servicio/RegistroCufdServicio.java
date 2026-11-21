package bo.org.citior.sife.servicio;

import bo.org.citior.sife.dominio.Cufd;
import bo.org.citior.sife.dominio.PuntoVenta;
import bo.org.citior.sife.dominio.dto.CufdRespuesta;
import bo.org.citior.sife.mapper.CufdMapper;
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
public class RegistroCufdServicio {

    private final CufdMapper cufdMapper;
    private final ObjectMapper objectMapper;

    @Transactional
    public Long registrar(CufdRespuesta respuesta, PuntoVenta puntoVenta, Long cuisId) {
        Long empresaId = respuesta.empresaId() == null
                ? EmpresaServicio.EMPRESA_ID
                : respuesta.empresaId();

        Cufd cufd = Cufd.builder()
                .empresaId(empresaId)
                .sucursalId(puntoVenta.getSucursalId())
                .puntoventaId(puntoVenta.getId())
                .cuisId(cuisId)
                .codigoSucursal(respuesta.codigoSucursal())
                .codigoPuntoVenta(respuesta.codigoPuntoVenta())
                .cufd(respuesta.cufd() == null ? "" : respuesta.cufd())
                .codigoControl(respuesta.codigoControl())
                .direccion(respuesta.direccion())
                .fechaVigencia(parsearFecha(respuesta.fechaVigencia()))
                .transaccion(respuesta.transaccion())
                .mensajes(serializar(respuesta.mensajes()))
                .build();

        cufdMapper.insert(cufd);
        cufdMapper.activarVigente(empresaId, puntoVenta.getId());
        return cufd.getId();
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

    private String serializar(List<CufdRespuesta.MensajeCufd> mensajes) {
        try {
            return objectMapper.writeValueAsString(
                    mensajes == null ? List.of() : mensajes);
        } catch (JacksonException excepcion) {
            return "[]";
        }
    }
}
