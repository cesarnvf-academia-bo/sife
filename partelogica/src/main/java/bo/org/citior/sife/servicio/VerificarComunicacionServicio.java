package bo.org.citior.sife.servicio;

import bo.gob.impuesto.siat.codigos.ServicioFacturacionCodigos;
import bo.gob.impuesto.siat.compraventa.ServicioFacturacion;
import bo.gob.impuesto.siat.operaciones.ServicioFacturacionOperaciones;
import bo.gob.impuesto.siat.sincronizacion.ServicioFacturacionSincronizacion;
import bo.org.citior.sife.configuracion.PropiedadesSiat;
import bo.org.citior.sife.dominio.Empresa;
import bo.org.citior.sife.dominio.dto.RespuestaVerificacionComunicacion;
import bo.org.citior.sife.dominio.dto.ResultadoServicioSiat;
import bo.org.citior.sife.excepcion.EmpresaNoEncontradaExcepcion;
import bo.org.citior.sife.excepcion.TokenSiatNoConfiguradoExcepcion;
import bo.org.citior.sife.repositorio.EmpresaRepositorio;
import bo.org.citior.sife.siat.ClienteSiatFactory;
import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class VerificarComunicacionServicio {

    private final EmpresaRepositorio empresaRepositorio;
    private final ClienteSiatFactory clienteSiatFactory;
    private final PropiedadesSiat propiedades;

    public RespuestaVerificacionComunicacion verificar(Long empresaId) {
        Empresa empresa = empresaRepositorio.findById(empresaId)
                .orElseThrow(() -> new EmpresaNoEncontradaExcepcion(empresaId));

        if (empresa.getToken() == null || empresa.getToken().isBlank()) {
            throw new TokenSiatNoConfiguradoExcepcion(empresaId);
        }

        String token = empresa.getToken().trim();
        List<ResultadoServicioSiat> resultados = List.of(
                verificarCodigos(token),
                verificarSincronizacion(token),
                verificarOperaciones(token),
                verificarCompraventa(token));

        return new RespuestaVerificacionComunicacion(
                empresa.getId(),
                empresa.getNit(),
                Instant.now(),
                resultados);
    }

    private ResultadoServicioSiat verificarCodigos(String token) {
        return verificar("FacturacionCodigos", () -> {
            var respuesta = clienteSiatFactory
                    .crearPuerto(
                            ServicioFacturacionCodigos.class,
                            propiedades.codigosUrl(),
                            token)
                    .verificarComunicacion();
            return extraerDatos(
                    respuesta.isTransaccion(),
                    respuesta.getMensajesList(),
                    mensaje -> mensaje.getCodigo(),
                    mensaje -> mensaje.getDescripcion());
        });
    }

    private ResultadoServicioSiat verificarSincronizacion(String token) {
        return verificar("FacturacionSincronizacion", () -> {
            var respuesta = clienteSiatFactory
                    .crearPuerto(
                            ServicioFacturacionSincronizacion.class,
                            propiedades.sincronizacionUrl(),
                            token)
                    .verificarComunicacion();
            return extraerDatos(
                    respuesta.isTransaccion(),
                    respuesta.getMensajesList(),
                    mensaje -> mensaje.getCodigo(),
                    mensaje -> mensaje.getDescripcion());
        });
    }

    private ResultadoServicioSiat verificarOperaciones(String token) {
        return verificar("FacturacionOperaciones", () -> {
            var respuesta = clienteSiatFactory
                    .crearPuerto(
                            ServicioFacturacionOperaciones.class,
                            propiedades.operacionesUrl(),
                            token)
                    .verificarComunicacion();
            return extraerDatos(
                    respuesta.isTransaccion(),
                    respuesta.getMensajesList(),
                    mensaje -> mensaje.getCodigo(),
                    mensaje -> mensaje.getDescripcion());
        });
    }

    private ResultadoServicioSiat verificarCompraventa(String token) {
        return verificar("FacturacionCompraventa", () -> {
            var respuesta = clienteSiatFactory
                    .crearPuerto(
                            ServicioFacturacion.class,
                            propiedades.compraventaUrl(),
                            token)
                    .verificarComunicacion();
            return extraerDatos(
                    respuesta.isTransaccion(),
                    respuesta.getMensajesList(),
                    mensaje -> mensaje.getCodigo(),
                    mensaje -> mensaje.getDescripcion());
        });
    }

    private ResultadoServicioSiat verificar(
            String servicio, Supplier<DatosRespuestaSiat> invocacion) {
        try {
            DatosRespuestaSiat datos = invocacion.get();
            return new ResultadoServicioSiat(
                    servicio, datos.comunicacion(), datos.codigo(), datos.mensaje());
        } catch (Exception excepcion) {
            return new ResultadoServicioSiat(
                    servicio, false, null, mensajeError(excepcion));
        }
    }

    private <M> DatosRespuestaSiat extraerDatos(
            Boolean transaccion,
            List<M> mensajes,
            Function<M, Integer> obtenerCodigo,
            Function<M, String> obtenerDescripcion) {
        boolean comunicacion = Boolean.TRUE.equals(transaccion);

        if (mensajes == null || mensajes.isEmpty()) {
            String mensaje = comunicacion
                    ? "Comunicación exitosa"
                    : "El SIAT no devolvió mensajes.";
            return new DatosRespuestaSiat(comunicacion, null, mensaje);
        }

        Integer codigo = mensajes.stream()
                .map(obtenerCodigo)
                .filter(Objects::nonNull)
                .findFirst()
                .orElse(null);

        String descripcion = mensajes.stream()
                .map(obtenerDescripcion)
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(texto -> !texto.isEmpty())
                .collect(Collectors.joining("; "));

        if (descripcion.isEmpty()) {
            descripcion = "Sin descripción del SIAT.";
        }

        return new DatosRespuestaSiat(comunicacion, codigo, descripcion);
    }

    private String mensajeError(Exception excepcion) {
        String mensaje = excepcion.getMessage();
        return mensaje == null || mensaje.isBlank()
                ? excepcion.getClass().getSimpleName()
                : mensaje;
    }

    private record DatosRespuestaSiat(
            boolean comunicacion,
            Integer codigo,
            String mensaje) {
    }
}
