package bo.org.citior.sife.mapper;

import bo.org.citior.sife.dominio.Cliente;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface ClienteMapper {

    Cliente findById(@Param("id") Long id);

    List<Cliente> listar(
            @Param("busqueda") String busqueda,
            @Param("offset") int offset,
            @Param("size") int size);

    long contar(@Param("busqueda") String busqueda);

    void insert(Cliente cliente);

    void update(Cliente cliente);

    void delete(@Param("id") Long id);

    boolean existsByCodigoInterno(String codigoInterno);

    boolean existsByCodigoInternoAndIdNot(
            @Param("codigoInterno") String codigoInterno,
            @Param("id") Long id);

    List<Cliente> listarActivos();
}
