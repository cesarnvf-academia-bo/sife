package bo.org.citior.sife.servicio;

import bo.org.citior.sife.dominio.PuntoVenta;
import bo.org.citior.sife.dominio.dto.SincronizacionResumen;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class TareasProgramadasServicio {

    private final PuntoVentaServicio puntoVentaServicio;
    private final SincronizarCatalogosServicio sincronizarCatalogosServicio;

    @Scheduled(cron = "0 0 4 * * *", zone = "America/La_Paz")
    public void ejecutarTareasDiarias() {
        List<PuntoVenta> puntosVenta = puntoVentaServicio.listarActivos();
        log.info(
                "Iniciando tareas diarias SIAT para {} puntos de venta activos",
                puntosVenta.size());

        for (PuntoVenta puntoVenta : puntosVenta) {
            generarCufd(puntoVenta);
            sincronizarCatalogos(puntoVenta);
        }

        log.info("Finalizando tareas diarias SIAT");
    }

    private void generarCufd(PuntoVenta puntoVenta) {
        try {
            puntoVentaServicio.obtenerCufd(puntoVenta.getId());
            log.info("CUFD generado para el punto de venta {}", puntoVenta.getId());
        } catch (Exception excepcion) {
            log.error(
                    "No se pudo generar el CUFD para el punto de venta {}",
                    puntoVenta.getId(),
                    excepcion);
        }
    }

    private void sincronizarCatalogos(PuntoVenta puntoVenta) {
        try {
            SincronizacionResumen resumen =
                    sincronizarCatalogosServicio.sincronizar(puntoVenta.getId());
            log.info(
                    "Catálogos sincronizados para el punto de venta {}: {} exitosas, {} fallidas",
                    puntoVenta.getId(),
                    resumen.exitosas(),
                    resumen.fallidas());
        } catch (Exception excepcion) {
            log.error(
                    "No se pudieron sincronizar los catálogos del punto de venta {}",
                    puntoVenta.getId(),
                    excepcion);
        }
    }
}
