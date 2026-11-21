package bo.org.citior.sife.mapper;

import bo.org.citior.sife.dominio.Cuis;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface CuisMapper {

    void insert(Cuis cuis);

    Cuis obtenerVigente(
            @Param("empresaId") Long empresaId,
            @Param("puntoventaId") Long puntoventaId);

    void activarVigente(
            @Param("empresaId") Long empresaId,
            @Param("puntoventaId") Long puntoventaId);
}
