package bo.org.citior.sife.servicio;

import bo.gob.impuesto.siat.sincronizacion.ActividadesDocumentoSectorDto;
import bo.gob.impuesto.siat.sincronizacion.ActividadesDto;
import bo.gob.impuesto.siat.sincronizacion.ParametricasDto;
import bo.gob.impuesto.siat.sincronizacion.ParametricaLeyendasDto;
import bo.gob.impuesto.siat.sincronizacion.ProductosDto;
import bo.gob.impuesto.siat.sincronizacion.RespuestaConfiguracion;
import bo.gob.impuesto.siat.sincronizacion.RespuestaFechaHora;
import bo.gob.impuesto.siat.sincronizacion.RespuestaListaActividades;
import bo.gob.impuesto.siat.sincronizacion.RespuestaListaActividadesDocumentoSector;
import bo.gob.impuesto.siat.sincronizacion.RespuestaListaParametricas;
import bo.gob.impuesto.siat.sincronizacion.RespuestaListaParametricasLeyendas;
import bo.gob.impuesto.siat.sincronizacion.RespuestaListaProductos;
import bo.org.citior.sife.dominio.SincronizacionEjecucion;
import bo.org.citior.sife.dominio.catalogo.Actividad;
import bo.org.citior.sife.dominio.catalogo.ActividadDocumentoSector;
import bo.org.citior.sife.dominio.catalogo.FechaHoraSiat;
import bo.org.citior.sife.dominio.catalogo.LeyendaFactura;
import bo.org.citior.sife.dominio.catalogo.Parametrica;
import bo.org.citior.sife.dominio.catalogo.ProductoServicio;
import bo.org.citior.sife.dominio.enumeracion.SincronizacionFuncion;
import bo.org.citior.sife.mapper.SincronizacionMapper;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SincronizacionPersistenciaServicio {

    private final SincronizacionMapper sincronizacionMapper;

    @Transactional
    public Long registrarEjecucion(
            SincronizacionEjecucion ejecucion,
            SincronizacionFuncion funcion,
            RespuestaConfiguracion respuesta) {
        sincronizacionMapper.insertarEjecucion(ejecucion);
        Long ejecucionId = ejecucion.getId();

        if (!"EXITOSA".equals(ejecucion.getEstado())
                || !ejecucion.isTransaccion()
                || respuesta == null) {
            return ejecucionId;
        }

        guardarCatalogo(ejecucionId, funcion, respuesta, ejecucion.getFechaHoraSiat());
        return ejecucionId;
    }

    private void guardarCatalogo(
            Long ejecucionId,
            SincronizacionFuncion funcion,
            RespuestaConfiguracion respuesta,
            String fechaHoraSiat) {
        switch (funcion.getTipo()) {
            case PARAMETRICA -> guardarParametricas(ejecucionId, funcion, respuesta);
            case ACTIVIDAD -> guardarActividades(ejecucionId, respuesta);
            case FECHA_HORA -> guardarFechaHora(ejecucionId, respuesta, fechaHoraSiat);
            case LEYENDA -> guardarLeyendas(ejecucionId, respuesta);
            case ACTIVIDAD_DOCUMENTO_SECTOR ->
                    guardarActividadesDocumentoSector(ejecucionId, respuesta);
            case PRODUCTO_SERVICIO -> guardarProductosServicios(ejecucionId, respuesta);
        }
    }

    private void guardarParametricas(
            Long ejecucionId, SincronizacionFuncion funcion, RespuestaConfiguracion respuesta) {
        if (!(respuesta instanceof RespuestaListaParametricas parametricas)) {
            return;
        }
        List<Parametrica> catalogo = deduplicar(
                parametricas.getListaCodigos(), ParametricasDto::getCodigoClasificador).stream()
                .map(this::aParametrica)
                .toList();
        catalogo.forEach(item -> sincronizacionMapper.insertarParametrica(
                ejecucionId,
                funcion.getNombre(),
                item.getCodigo(),
                item.getDescripcion()));
    }

    private Parametrica aParametrica(ParametricasDto dto) {
        return Parametrica.builder()
                .codigo(dto.getCodigoClasificador())
                .descripcion(dto.getDescripcion())
                .build();
    }

    private void guardarActividades(Long ejecucionId, RespuestaConfiguracion respuesta) {
        if (!(respuesta instanceof RespuestaListaActividades actividades)) {
            return;
        }
        List<Actividad> catalogo = deduplicar(
                actividades.getListaActividades(),
                dto -> Arrays.asList(dto.getCodigoCaeb(), dto.getTipoActividad())).stream()
                .map(this::aActividad)
                .toList();
        catalogo.forEach(item -> sincronizacionMapper.insertarActividad(
                ejecucionId,
                item.getCodigoCaeb(),
                item.getDescripcion(),
                item.getTipoActividad()));
    }

    private Actividad aActividad(ActividadesDto dto) {
        return Actividad.builder()
                .codigoCaeb(dto.getCodigoCaeb())
                .descripcion(dto.getDescripcion())
                .tipoActividad(dto.getTipoActividad())
                .build();
    }

    private void guardarFechaHora(
            Long ejecucionId, RespuestaConfiguracion respuesta, String fechaHoraSiat) {
        if (!(respuesta instanceof RespuestaFechaHora fechaHora)) {
            return;
        }
        FechaHoraSiat catalogo = FechaHoraSiat.builder()
                .fechaHoraSiat(fechaHora.getFechaHora() == null
                        ? fechaHoraSiat
                        : fechaHora.getFechaHora())
                .build();
        if (catalogo.getFechaHoraSiat() != null) {
            sincronizacionMapper.insertarFechaHora(ejecucionId, catalogo.getFechaHoraSiat());
        }
    }

    private void guardarLeyendas(Long ejecucionId, RespuestaConfiguracion respuesta) {
        if (!(respuesta instanceof RespuestaListaParametricasLeyendas leyendas)) {
            return;
        }
        List<LeyendaFactura> catalogo = deduplicar(
                leyendas.getListaLeyendas(),
                dto -> Arrays.asList(dto.getCodigoActividad(), dto.getDescripcionLeyenda())).stream()
                .map(this::aLeyenda)
                .toList();
        catalogo.forEach(item -> sincronizacionMapper.insertarLeyenda(
                ejecucionId,
                item.getCodigoActividad(),
                item.getDescripcionLeyenda()));
    }

    private LeyendaFactura aLeyenda(ParametricaLeyendasDto dto) {
        return LeyendaFactura.builder()
                .codigoActividad(dto.getCodigoActividad())
                .descripcionLeyenda(dto.getDescripcionLeyenda())
                .build();
    }

    private void guardarActividadesDocumentoSector(
            Long ejecucionId, RespuestaConfiguracion respuesta) {
        if (!(respuesta instanceof RespuestaListaActividadesDocumentoSector documentoSector)) {
            return;
        }
        List<ActividadDocumentoSector> catalogo = deduplicar(
                documentoSector.getListaActividadesDocumentoSector(),
                dto -> Arrays.asList(
                        dto.getCodigoActividad(),
                        dto.getCodigoDocumentoSector(),
                        dto.getTipoDocumentoSector())).stream()
                .map(this::aActividadDocumentoSector)
                .toList();
        catalogo.forEach(item -> sincronizacionMapper.insertarActividadDocumentoSector(
                ejecucionId,
                item.getCodigoActividad(),
                item.getCodigoDocumentoSector(),
                item.getTipoDocumentoSector()));
    }

    private ActividadDocumentoSector aActividadDocumentoSector(ActividadesDocumentoSectorDto dto) {
        return ActividadDocumentoSector.builder()
                .codigoActividad(dto.getCodigoActividad())
                .codigoDocumentoSector(dto.getCodigoDocumentoSector())
                .tipoDocumentoSector(dto.getTipoDocumentoSector())
                .build();
    }

    private void guardarProductosServicios(Long ejecucionId, RespuestaConfiguracion respuesta) {
        if (!(respuesta instanceof RespuestaListaProductos productos)) {
            return;
        }
        List<ProductoServicio> catalogo = deduplicar(
                productos.getListaCodigos(),
                dto -> Arrays.asList(dto.getCodigoActividad(), dto.getCodigoProducto())).stream()
                .map(dto -> aProductoServicio(ejecucionId, dto))
                .toList();
        catalogo.forEach(this::insertarProductoServicio);
    }

    private ProductoServicio aProductoServicio(Long ejecucionId, ProductosDto dto) {
        return ProductoServicio.builder()
                .ejecucionId(ejecucionId)
                .codigoActividad(dto.getCodigoActividad())
                .codigoProducto(dto.getCodigoProducto())
                .descripcionProducto(dto.getDescripcionProducto())
                .nandina(dto.getNandina())
                .build();
    }

    private void insertarProductoServicio(ProductoServicio producto) {
        Long generado = sincronizacionMapper.insertarProductoServicio(producto);
        Long productoId = generado == null ? producto.getId() : generado;
        if (productoId == null || producto.getNandina() == null) {
            return;
        }
        producto.getNandina().stream()
                .filter(valor -> valor != null && !valor.isBlank())
                .map(String::trim)
                .distinct()
                .forEach(valor -> sincronizacionMapper.insertarProductoNandina(
                        productoId, valor));
    }

    private static <T, K> List<T> deduplicar(List<T> valores, Function<T, K> clave) {
        Map<K, T> unicos = new LinkedHashMap<>();
        for (T valor : valores) {
            if (valor != null) {
                unicos.putIfAbsent(clave.apply(valor), valor);
            }
        }
        return List.copyOf(unicos.values());
    }
}
