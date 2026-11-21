package bo.org.citior.sife.mapper;

import bo.org.citior.sife.dominio.PuntoVenta;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface PuntoVentaMapper {

    PuntoVenta findById(@Param("id") Long id);

    List<PuntoVenta> listar(
            @Param("busqueda") String busqueda,
            @Param("offset") int offset,
            @Param("size") int size);

    List<PuntoVenta> listarActivos();

    long contar(@Param("busqueda") String busqueda);

    boolean existsBySucursalIdAndCodigo(
            @Param("sucursalId") Long sucursalId,
            @Param("codigo") String codigo);

    boolean existsBySucursalIdAndCodigoAndIdNot(
            @Param("sucursalId") Long sucursalId,
            @Param("codigo") String codigo,
            @Param("id") Long id);

    void insert(PuntoVenta puntoVenta);

    void update(PuntoVenta puntoVenta);

    void delete(@Param("id") Long id);
}
