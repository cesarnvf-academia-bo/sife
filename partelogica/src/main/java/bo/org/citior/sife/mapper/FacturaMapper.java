package bo.org.citior.sife.mapper;

import bo.org.citior.sife.dominio.Factura;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FacturaMapper {

    void insertar(Factura factura);

    Factura obtenerPorId(@Param("id") Long id);

    List<Factura> listar(
            @Param("busqueda") String busqueda,
            @Param("offset") int offset,
            @Param("size") int size);

    long contar(@Param("busqueda") String busqueda);

    Long siguienteNumeroFactura();

    void actualizarRespuesta(@Param("id") Long id, @Param("factura") Factura factura);

    void actualizarAnulacion(@Param("id") Long id, @Param("factura") Factura factura);

    void actualizarReversion(@Param("id") Long id, @Param("factura") Factura factura);
}
