package bo.org.citior.sife.mapper;

import bo.org.citior.sife.dominio.FacturaDetalle;
import java.util.List;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface FacturaDetalleMapper {

    void insertarLote(@Param("facturaId") Long facturaId, @Param("detalles") List<FacturaDetalle> detalles);

    List<FacturaDetalle> listarPorFacturaId(Long facturaId);
}
