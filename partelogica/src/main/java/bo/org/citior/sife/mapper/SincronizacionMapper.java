package bo.org.citior.sife.mapper;

import bo.org.citior.sife.dominio.SincronizacionEjecucion;
import bo.org.citior.sife.dominio.catalogo.ProductoServicio;
import bo.org.citior.sife.dominio.dto.CatalogoFila;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SincronizacionMapper {

    Long insertarEjecucion(SincronizacionEjecucion ejecucion);

    SincronizacionEjecucion obtenerUltimaEjecucionExitosa(
            @Param("empresaId") Long empresaId,
            @Param("puntoventaId") Long puntoventaId,
            @Param("funcion") String funcion);

    void insertarParametrica(
            @Param("ejecucionId") Long ejecucionId,
            @Param("funcion") String funcion,
            @Param("codigo") Integer codigo,
            @Param("descripcion") String descripcion);

    void insertarActividad(
            @Param("ejecucionId") Long ejecucionId,
            @Param("codigoCaeb") String codigoCaeb,
            @Param("descripcion") String descripcion,
            @Param("tipoActividad") String tipoActividad);

    void insertarFechaHora(
            @Param("ejecucionId") Long ejecucionId,
            @Param("fechaHoraSiat") String fechaHoraSiat);

    void insertarLeyenda(
            @Param("ejecucionId") Long ejecucionId,
            @Param("codigoActividad") String codigoActividad,
            @Param("descripcionLeyenda") String descripcionLeyenda);

    void insertarActividadDocumentoSector(
            @Param("ejecucionId") Long ejecucionId,
            @Param("codigoActividad") String codigoActividad,
            @Param("codigoDocumentoSector") Integer codigoDocumentoSector,
            @Param("tipoDocumentoSector") String tipoDocumentoSector);

    Long insertarProductoServicio(ProductoServicio producto);

    void insertarProductoNandina(
            @Param("productoId") Long productoId,
            @Param("nandina") String nandina);

    List<CatalogoFila> listarCatalogo(
            @Param("ejecucionId") Long ejecucionId,
            @Param("tipo") String tipo);
}
