package bo.org.citior.sife.servicio;

import bo.org.citior.sife.dominio.dto.ActividadOpcion;
import bo.org.citior.sife.dominio.dto.ProductoServicioOpcion;
import bo.org.citior.sife.dominio.dto.UnidadMedidaOpcion;
import bo.org.citior.sife.mapper.SincronizacionMapper;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CatalogoProductoServicio {

    private final SincronizacionMapper sincronizacionMapper;

    @Transactional(readOnly = true)
    public List<ActividadOpcion> listarActividades() {
        return sincronizacionMapper.listarActividades();
    }

    @Transactional(readOnly = true)
    public List<ProductoServicioOpcion> listarProductosServicio() {
        return sincronizacionMapper.listarProductosServicio();
    }

    @Transactional(readOnly = true)
    public List<UnidadMedidaOpcion> listarUnidadesMedida() {
        return sincronizacionMapper.listarUnidadesMedida();
    }

    @Transactional(readOnly = true)
    public String descripcionActividad(String codigo) {
        if (codigo == null) {
            return null;
        }
        return listarActividades().stream()
                .filter(opcion -> codigo.equals(opcion.codigo()))
                .map(ActividadOpcion::descripcion)
                .findFirst()
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public String descripcionProductoServicio(Long codigo) {
        if (codigo == null) {
            return null;
        }
        return listarProductosServicio().stream()
                .filter(opcion -> codigo.equals(opcion.codigo()))
                .map(ProductoServicioOpcion::descripcion)
                .findFirst()
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public String descripcionUnidadMedida(Integer codigo) {
        if (codigo == null) {
            return null;
        }
        return listarUnidadesMedida().stream()
                .filter(opcion -> codigo.equals(opcion.codigo()))
                .map(UnidadMedidaOpcion::descripcion)
                .findFirst()
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public boolean existeActividad(String codigo) {
        return codigo != null && listarActividades().stream()
                .anyMatch(opcion -> codigo.equals(opcion.codigo()));
    }

    @Transactional(readOnly = true)
    public boolean existeProductoServicio(Long codigo) {
        return codigo != null && listarProductosServicio().stream()
                .anyMatch(opcion -> codigo.equals(opcion.codigo()));
    }

    @Transactional(readOnly = true)
    public boolean existeUnidadMedida(Integer codigo) {
        return codigo != null && listarUnidadesMedida().stream()
                .anyMatch(opcion -> codigo.equals(opcion.codigo()));
    }
}
