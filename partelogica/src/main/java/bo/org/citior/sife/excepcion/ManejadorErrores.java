package bo.org.citior.sife.excepcion;

import bo.org.citior.sife.dominio.dto.MensajeSiat;
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

    @ExceptionHandler(ClienteNoEncontradoExcepcion.class)
    public ProblemDetail manejarClienteNoEncontrado(ClienteNoEncontradoExcepcion excepcion) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, excepcion.getMessage());
        problema.setTitle("Cliente no encontrado");
        problema.setProperty("id", excepcion.getId());
        return problema;
    }

    @ExceptionHandler(ClienteCodigoInternoDuplicadoExcepcion.class)
    public ProblemDetail manejarClienteCodigoInternoDuplicado(
            ClienteCodigoInternoDuplicadoExcepcion excepcion) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, excepcion.getMessage());
        problema.setTitle("Código interno de cliente duplicado");
        problema.setProperty("codigoInterno", excepcion.getCodigoInterno());
        return problema;
    }

    @ExceptionHandler(TipoDocumentoIdentidadNoValidoExcepcion.class)
    public ProblemDetail manejarTipoDocumentoIdentidadNoValido(
            TipoDocumentoIdentidadNoValidoExcepcion excepcion) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, excepcion.getMessage());
        problema.setTitle("Tipo de documento de identidad no válido");
        problema.setProperty("codigo", excepcion.getCodigo());
        return problema;
    }

    @ExceptionHandler(ProductoNoEncontradoExcepcion.class)
    public ProblemDetail manejarProductoNoEncontrado(ProductoNoEncontradoExcepcion excepcion) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, excepcion.getMessage());
        problema.setTitle("Producto no encontrado");
        problema.setProperty("id", excepcion.getId());
        return problema;
    }

    @ExceptionHandler(ProductoCodigoInternoDuplicadoExcepcion.class)
    public ProblemDetail manejarProductoCodigoInternoDuplicado(
            ProductoCodigoInternoDuplicadoExcepcion excepcion) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, excepcion.getMessage());
        problema.setTitle("Código interno de producto duplicado");
        problema.setProperty("codigoInterno", excepcion.getCodigoInterno());
        return problema;
    }

    @ExceptionHandler(CatalogoProductoNoValidoExcepcion.class)
    public ProblemDetail manejarCatalogoProductoNoValido(
            CatalogoProductoNoValidoExcepcion excepcion) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, excepcion.getMessage());
        problema.setTitle("Catálogo de producto no válido");
        problema.setProperty("campo", excepcion.getCampo());
        problema.setProperty("valor", excepcion.getValor());
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

    @ExceptionHandler(FacturaNoEncontradaExcepcion.class)
    public ProblemDetail manejarFacturaNoEncontrada(FacturaNoEncontradaExcepcion excepcion) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.NOT_FOUND, excepcion.getMessage());
        problema.setTitle("Factura no encontrada");
        problema.setProperty("id", excepcion.getId());
        return problema;
    }

    @ExceptionHandler(FacturaEstadoInvalidoExcepcion.class)
    public ProblemDetail manejarFacturaEstadoInvalido(FacturaEstadoInvalidoExcepcion excepcion) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, excepcion.getMessage());
        problema.setTitle("Estado de factura inválido");
        problema.setProperty("id", excepcion.getId());
        problema.setProperty("estadoEsperado", excepcion.getEstadoEsperado());
        problema.setProperty("estadoActual", excepcion.getEstadoActual());
        return problema;
    }

    @ExceptionHandler(FirmaDigitalNoConfiguradaExcepcion.class)
    public ProblemDetail manejarFirmaDigitalNoConfigurada(FirmaDigitalNoConfiguradaExcepcion excepcion) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.CONFLICT, excepcion.getMessage());
        problema.setTitle("Firma digital no configurada");
        problema.setProperty("detalle", excepcion.getDetalle());
        return problema;
    }

    @ExceptionHandler(XmlFacturaInvalidoExcepcion.class)
    public ProblemDetail manejarXmlFacturaInvalido(XmlFacturaInvalidoExcepcion excepcion) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, excepcion.getMessage());
        problema.setTitle("XML de factura inválido");
        problema.setProperty("detalle", excepcion.getDetalle());
        return problema;
    }

    @ExceptionHandler(RespuestaSiatRechazadaExcepcion.class)
    public ProblemDetail manejarRespuestaSiatRechazada(RespuestaSiatRechazadaExcepcion excepcion) {
        ProblemDetail problema = ProblemDetail.forStatusAndDetail(
                HttpStatus.BAD_REQUEST, excepcion.getMessage());
        problema.setTitle("Factura rechazada por el SIAT");
        problema.setProperty("detalle", excepcion.getDetalle());
        problema.setProperty("mensajes", excepcion.getMensajes());
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
