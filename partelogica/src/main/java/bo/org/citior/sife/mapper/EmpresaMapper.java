package bo.org.citior.sife.mapper;

import bo.org.citior.sife.dominio.Empresa;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface EmpresaMapper {

    Empresa findById(@Param("id") Long id);

    void insert(Empresa empresa);

    void update(Empresa empresa);
}
