package bo.org.citior.sife.servicio;

import bo.gob.impuesto.siat.sincronizacion.RespuestaConfiguracion;
import bo.gob.impuesto.siat.sincronizacion.RespuestaFechaHora;
import bo.gob.impuesto.siat.sincronizacion.RespuestaListaActividades;
import bo.gob.impuesto.siat.sincronizacion.RespuestaListaActividadesDocumentoSector;
import bo.gob.impuesto.siat.sincronizacion.RespuestaListaParametricas;
import bo.gob.impuesto.siat.sincronizacion.RespuestaListaParametricasLeyendas;
import bo.gob.impuesto.siat.sincronizacion.RespuestaListaProductos;
import bo.gob.impuesto.siat.sincronizacion.ServicioFacturacionSincronizacion;
import bo.org.citior.sife.dominio.Cuis;
import bo.org.citior.sife.dominio.Empresa;
import bo.org.citior.sife.dominio.PuntoVenta;
import bo.org.citior.sife.dominio.SincronizacionEjecucion;
import bo.org.citior.sife.dominio.Sucursal;
import bo.org.citior.sife.dominio.dto.CuisRespuesta;
import bo.org.citior.sife.dominio.dto.SincronizacionFuncionRespuesta;
import bo.org.citior.sife.dominio.dto.SincronizacionFuncionResultado;
import bo.org.citior.sife.dominio.dto.SincronizacionResumen;
import bo.org.citior.sife.dominio.dto.SolicitudSincronizacionDatos;
import bo.org.citior.sife.dominio.enumeracion.SincronizacionFuncion;
import bo.org.citior.sife.excepcion.ConfiguracionSiatInvalidaExcepcion;
import bo.org.citior.sife.excepcion.SiatNoDisponibleExcepcion;
import bo.org.citior.sife.excepcion.SucursalNoEncontradaExcepcion;
import bo.org.citior.sife.mapper.CuisMapper;
import bo.org.citior.sife.mapper.SucursalMapper;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

@Service
@RequiredArgsConstructor
public class SincronizarCatalogosServicio {

    private static final String EXITOSA = "EXITOSA";
    private static final String FALLIDA = "FALLIDA";

    private final PuntoVentaServicio puntoVentaServicio;
    private final SucursalMapper sucursalMapper;
    private final CuisMapper cuisMapper;
    private final ObtenerCuisServicio obtenerCuisServicio;
    private final RegistroCuisServicio registroCuisServicio;
    private final SolicitudSincronizacionServicio solicitudSincronizacionServicio;
    private final SincronizacionSiatServicio sincronizacionSiatServicio;
    private final SincronizacionPersistenciaServicio sincronizacionPersistenciaServicio;
    private final ObjectMapper objectMapper;

    public List<SincronizacionFuncionRespuesta> listarFunciones() {
        return Arrays.stream(SincronizacionFuncion.values())
                .map(funcion -> new SincronizacionFuncionRespuesta(
                        funcion.getNombre(), funcion.getEtiqueta(), funcion.getTipo()))
                .toList();
    }

    public Long registrarEjecucion(
            SincronizacionEjecucion ejecucion,
            SincronizacionFuncion funcion,
            RespuestaConfiguracion respuesta) {
        return sincronizacionPersistenciaServicio.registrarEjecucion(ejecucion, funcion, respuesta);
    }

    public SincronizacionResumen sincronizar(Long puntoVentaId) {
        PuntoVenta puntoVenta = puntoVentaServicio.obtenerModelo(puntoVentaId);
        Sucursal sucursal = sucursalMapper.findById(puntoVenta.getSucursalId());
        if (sucursal == null) {
            throw new SucursalNoEncontradaExcepcion(puntoVenta.getSucursalId());
        }

        String cuis = resolverCuis(sucursal, puntoVenta);
        SolicitudSincronizacionDatos datos = solicitudSincronizacionServicio.construir(
                sucursal, puntoVenta, cuis);
        ServicioFacturacionSincronizacion puerto =
                sincronizacionSiatServicio.crearPuerto(datos.empresa());

        List<SincronizacionFuncionResultado> resultados = new ArrayList<>();
        int exitosas = 0;
        int fallidas = 0;

        for (SincronizacionFuncion funcion : SincronizacionFuncion.values()) {
            try {
                RespuestaConfiguracion respuesta = sincronizacionSiatServicio.invocar(
                        puerto, funcion, datos.solicitud());
                boolean transaccion = Boolean.TRUE.equals(respuesta.isTransaccion());
                String estado = transaccion ? EXITOSA : FALLIDA;
                List<SincronizacionFuncionResultado.MensajeRespuesta> mensajes =
                        extraerMensajes(respuesta);
                int registros = transaccion ? contarRegistros(funcion, respuesta) : 0;

                registrarEjecucion(
                        construirEjecucion(datos.empresa(), sucursal, puntoVenta, funcion,
                                estado, transaccion, mensajes, fechaHora(respuesta), null),
                        funcion,
                        respuesta);

                resultados.add(new SincronizacionFuncionResultado(
                        funcion.getNombre(), funcion.getEtiqueta(), estado, registros,
                        null, mensajes));

                if (transaccion) {
                    exitosas++;
                } else {
                    fallidas++;
                }
            } catch (SiatNoDisponibleExcepcion excepcion) {
                registrarEjecucion(
                        construirEjecucion(datos.empresa(), sucursal, puntoVenta, funcion,
                                FALLIDA, false, List.of(), null, excepcion.getMessage()),
                        funcion,
                        null);

                resultados.add(new SincronizacionFuncionResultado(
                        funcion.getNombre(), funcion.getEtiqueta(), FALLIDA, 0,
                        excepcion.getMessage(), List.of()));
                fallidas++;
            }
        }

        return new SincronizacionResumen(
                puntoVentaId,
                cuis.trim(),
                SincronizacionFuncion.values().length,
                exitosas,
                fallidas,
                List.copyOf(resultados));
    }

    private SincronizacionEjecucion construirEjecucion(
            Empresa empresa,
            Sucursal sucursal,
            PuntoVenta puntoVenta,
            SincronizacionFuncion funcion,
            String estado,
            boolean transaccion,
            List<SincronizacionFuncionResultado.MensajeRespuesta> mensajes,
            String fechaHoraSiat,
            String error) {
        return SincronizacionEjecucion.builder()
                .empresaId(empresa.getId())
                .sucursalId(sucursal.getId())
                .puntoventaId(puntoVenta.getId())
                .funcion(funcion.getNombre())
                .estado(estado)
                .transaccion(transaccion)
                .mensajes(serializar(mensajes))
                .fechaHoraSiat(fechaHoraSiat)
                .error(error)
                .build();
    }

    private String resolverCuis(Sucursal sucursal, PuntoVenta puntoVenta) {
        Cuis vigente = cuisMapper.obtenerVigente(
                EmpresaServicio.EMPRESA_ID, puntoVenta.getId());
        if (vigente != null && vigente.getCuis() != null && !vigente.getCuis().isBlank()) {
            return vigente.getCuis();
        }

        int codigoSucursal = parsearCodigo(sucursal.getCodigo(), "sucursal.codigo");
        int codigoPuntoVenta = parsearCodigo(puntoVenta.getCodigo(), "puntoventa.codigo");
        CuisRespuesta respuesta = obtenerCuisServicio.obtener(
                EmpresaServicio.EMPRESA_ID, codigoSucursal, codigoPuntoVenta);
        if (respuesta == null || respuesta.cuis() == null || respuesta.cuis().isBlank()) {
            throw new SiatNoDisponibleExcepcion(
                    "FacturacionSincronizacion", "CUIS no disponible");
        }
        registroCuisServicio.registrar(respuesta, puntoVenta);
        return respuesta.cuis();
    }

    private int parsearCodigo(String valor, String campo) {
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException | NullPointerException excepcion) {
            throw new ConfiguracionSiatInvalidaExcepcion(
                    EmpresaServicio.EMPRESA_ID, campo);
        }
    }

    private int contarRegistros(
            SincronizacionFuncion funcion, RespuestaConfiguracion respuesta) {
        return switch (funcion.getTipo()) {
            case PARAMETRICA -> respuesta instanceof RespuestaListaParametricas parametricas
                    ? parametricas.getListaCodigos().size()
                    : 0;
            case ACTIVIDAD -> respuesta instanceof RespuestaListaActividades actividades
                    ? actividades.getListaActividades().size()
                    : 0;
            case FECHA_HORA -> 1;
            case LEYENDA -> respuesta instanceof RespuestaListaParametricasLeyendas leyendas
                    ? leyendas.getListaLeyendas().size()
                    : 0;
            case ACTIVIDAD_DOCUMENTO_SECTOR ->
                    respuesta instanceof RespuestaListaActividadesDocumentoSector documentoSector
                            ? documentoSector.getListaActividadesDocumentoSector().size()
                            : 0;
            case PRODUCTO_SERVICIO -> respuesta instanceof RespuestaListaProductos productos
                    ? productos.getListaCodigos().size()
                    : 0;
        };
    }

    private String fechaHora(RespuestaConfiguracion respuesta) {
        return respuesta instanceof RespuestaFechaHora fechaHora
                ? fechaHora.getFechaHora()
                : null;
    }

    private List<SincronizacionFuncionResultado.MensajeRespuesta> extraerMensajes(
            RespuestaConfiguracion respuesta) {
        return respuesta.getMensajesList().stream()
                .map(mensaje -> new SincronizacionFuncionResultado.MensajeRespuesta(
                        mensaje.getCodigo(), mensaje.getDescripcion()))
                .toList();
    }

    private String serializar(
            List<SincronizacionFuncionResultado.MensajeRespuesta> mensajes) {
        try {
            return objectMapper.writeValueAsString(mensajes == null ? List.of() : mensajes);
        } catch (JacksonException excepcion) {
            return "[]";
        }
    }
}
