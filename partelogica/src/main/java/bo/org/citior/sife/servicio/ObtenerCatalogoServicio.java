package bo.org.citior.sife.servicio;

import bo.org.citior.sife.dominio.SincronizacionEjecucion;
import bo.org.citior.sife.dominio.dto.CatalogoFila;
import bo.org.citior.sife.dominio.dto.CatalogoRespuesta;
import bo.org.citior.sife.dominio.dto.SincronizacionFuncionRespuesta;
import bo.org.citior.sife.dominio.enumeracion.SincronizacionFuncion;
import bo.org.citior.sife.dominio.enumeracion.TipoCatalogo;
import bo.org.citior.sife.mapper.SincronizacionMapper;
import java.util.Arrays;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ObtenerCatalogoServicio {

    private final PuntoVentaServicio puntoVentaServicio;
    private final SincronizacionMapper sincronizacionMapper;

    @Transactional(readOnly = true)
    public List<SincronizacionFuncionRespuesta> listarFunciones() {
        return Arrays.stream(SincronizacionFuncion.values())
                .map(funcion -> new SincronizacionFuncionRespuesta(
                        funcion.getNombre(), funcion.getEtiqueta(), funcion.getTipo()))
                .toList();
    }

    @Transactional(readOnly = true)
    public CatalogoRespuesta obtener(Long puntoVentaId, String nombreFuncion) {
        SincronizacionFuncion funcion = SincronizacionFuncion.desdeNombre(nombreFuncion);
        puntoVentaServicio.obtenerModelo(puntoVentaId);

        List<String> columnas = columnas(funcion.getTipo());
        SincronizacionEjecucion ejecucion =
                sincronizacionMapper.obtenerUltimaEjecucionExitosa(
                        EmpresaServicio.EMPRESA_ID, puntoVentaId, funcion.getNombre());

        if (ejecucion == null) {
            return new CatalogoRespuesta(
                    funcion.getNombre(), funcion.getEtiqueta(), null, null, columnas, List.of());
        }

        List<CatalogoFila> filas = sincronizacionMapper.listarCatalogo(
                ejecucion.getId(), funcion.getTipo().name());
        String ejecutadoEn = ejecucion.getEjecutadoEn() == null
                ? null
                : ejecucion.getEjecutadoEn().toString();

        return new CatalogoRespuesta(
                funcion.getNombre(),
                funcion.getEtiqueta(),
                ejecucion.getId(),
                ejecutadoEn,
                columnas,
                filas);
    }

    private List<String> columnas(TipoCatalogo tipo) {
        return switch (tipo) {
            case PARAMETRICA -> List.of("Código", "Descripción");
            case ACTIVIDAD -> List.of("Código CAEB", "Descripción", "Tipo de actividad");
            case FECHA_HORA -> List.of("Fecha y hora SIAT");
            case LEYENDA -> List.of("Código de actividad", "Leyenda");
            case ACTIVIDAD_DOCUMENTO_SECTOR -> List.of(
                    "Código de actividad",
                    "Código de documento sector",
                    "Tipo de documento sector");
            case PRODUCTO_SERVICIO -> List.of(
                    "Código de actividad", "Producto", "Código de producto", "Nandina");
        };
    }
}
