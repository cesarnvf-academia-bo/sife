package bo.org.citior.sife.mapper;

import bo.org.citior.sife.dominio.Producto;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ProductoMapper {

    long contar(@Param("busqueda") String busqueda);

    List<Producto> listar(
            @Param("busqueda") String busqueda,
            @Param("offset") int offset,
            @Param("size") int size);

    Producto findById(Long id);

    boolean existsByCodigoInterno(String codigoInterno);

    boolean existsByCodigoInternoAndIdNot(
            @Param("codigoInterno") String codigoInterno,
            @Param("id") Long id);

    void insert(Producto producto);

    void update(Producto producto);

    void delete(Long id);
}
