package bo.org.citior.sife.mapper;

import bo.org.citior.sife.dominio.Cufd;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CufdMapper {

    void insert(Cufd cufd);

    void activarVigente(
            @Param("empresaId") Long empresaId,
            @Param("puntoventaId") Long puntoventaId);
}
