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

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail manejarSolicitudInvalida(MethodArgumentNotValidException excepcion) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, "Los datos de la empresa no son válidos.");
        problema.setTitle("Solicitud inválida");

        Map<String, String> errores = new LinkedHashMap<>();
        excepcion.getBindingResult().getFieldErrors().forEach(error ->
                errores.putIfAbsent(error.getField(), error.getDefaultMessage()));
        problema.setProperty("errores", errores);

        return problema;
    }
}
