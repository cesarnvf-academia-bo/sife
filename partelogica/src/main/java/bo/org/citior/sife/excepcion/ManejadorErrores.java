package bo.org.citior.sife.excepcion;

import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ManejadorErrores {

    @ExceptionHandler(EmpresaNoEncontradaExcepcion.class)
    public ProblemDetail manejarEmpresaNoEncontrada(EmpresaNoEncontradaExcepcion excepcion) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, excepcion.getMessage());
        problema.setTitle("Empresa no encontrada");
        problema.setProperty("id", excepcion.getId());
        return problema;
    }

    @ExceptionHandler(SucursalNoEncontradaExcepcion.class)
    public ProblemDetail manejarSucursalNoEncontrada(SucursalNoEncontradaExcepcion excepcion) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, excepcion.getMessage());
        problema.setTitle("Sucursal no encontrada");
        problema.setProperty("id", excepcion.getId());
        return problema;
    }

    @ExceptionHandler(SucursalCodigoDuplicadoExcepcion.class)
    public ProblemDetail manejarCodigoDuplicado(SucursalCodigoDuplicadoExcepcion excepcion) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, excepcion.getMessage());
        problema.setTitle("Código de sucursal duplicado");
        problema.setProperty("codigo", excepcion.getCodigo());
        return problema;
    }

    @ExceptionHandler(PuntoVentaNoEncontradoExcepcion.class)
    public ProblemDetail manejarPuntoVentaNoEncontrado(
            PuntoVentaNoEncontradoExcepcion excepcion) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, excepcion.getMessage());
        problema.setTitle("Punto de venta no encontrado");
        problema.setProperty("id", excepcion.getId());
        return problema;
    }

    @ExceptionHandler(PuntoVentaCodigoDuplicadoExcepcion.class)
    public ProblemDetail manejarCodigoPuntoVentaDuplicado(
            PuntoVentaCodigoDuplicadoExcepcion excepcion) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, excepcion.getMessage());
        problema.setTitle("Código de punto de venta duplicado");
        problema.setProperty("codigo", excepcion.getCodigo());
        return problema;
    }

    @ExceptionHandler(TokenSiatNoConfiguradoExcepcion.class)
    public ProblemDetail manejarTokenNoConfigurado(TokenSiatNoConfiguradoExcepcion excepcion) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, excepcion.getMessage());
        problema.setTitle("Token SIAT no configurado");
        problema.setProperty("empresaId", excepcion.getEmpresaId());
        return problema;
    }

    @ExceptionHandler(ConfiguracionSiatInvalidaExcepcion.class)
    public ProblemDetail manejarConfiguracionSiatInvalida(
            ConfiguracionSiatInvalidaExcepcion excepcion) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, excepcion.getMessage());
        problema.setTitle("Configuración SIAT inválida");
        problema.setProperty("empresaId", excepcion.getEmpresaId());
        problema.setProperty("campo", excepcion.getCampo());
        return problema;
    }

    @ExceptionHandler(SiatNoDisponibleExcepcion.class)
    public ProblemDetail manejarSiatNoDisponible(SiatNoDisponibleExcepcion excepcion) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_GATEWAY, excepcion.getMessage());
        problema.setTitle("Servicio SIAT no disponible");
        problema.setProperty("servicio", excepcion.getServicio());
        return problema;
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ProblemDetail manejarArgumentoInvalido(IllegalArgumentException excepcion) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, excepcion.getMessage());
        problema.setTitle("Solicitud inválida");
        problema.setProperty("errores", Map.of("size", excepcion.getMessage()));
        return problema;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail manejarSolicitudInvalida(MethodArgumentNotValidException excepcion) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Los datos de la solicitud no son válidos.");
        problema.setTitle("Solicitud inválida");

        Map<String, String> errores = new LinkedHashMap<>();
        excepcion.getBindingResult().getFieldErrors().forEach(error ->
                errores.putIfAbsent(error.getField(), error.getDefaultMessage()));
        problema.setProperty("errores", errores);

        return problema;
    }
}
