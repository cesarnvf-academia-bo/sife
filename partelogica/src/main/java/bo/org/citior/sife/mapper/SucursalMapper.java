package bo.org.citior.sife.mapper;

import bo.org.citior.sife.dominio.Sucursal;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface SucursalMapper {

    Sucursal findById(@Param("id") Long id);

    List<Sucursal> listar(
            @Param("busqueda") String busqueda,
            @Param("offset") int offset,
            @Param("size") int size);

    long contar(@Param("busqueda") String busqueda);

    boolean existsByCodigo(@Param("codigo") String codigo);

    boolean existsByCodigoAndIdNot(
            @Param("codigo") String codigo,
            @Param("id") Long id);

    void insert(Sucursal sucursal);

    void update(Sucursal sucursal);

    void delete(@Param("id") Long id);

    List<Sucursal> listarActivos();
}
