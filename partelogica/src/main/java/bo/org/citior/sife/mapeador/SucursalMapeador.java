package bo.org.citior.sife.mapeador;

import bo.org.citior.sife.dominio.Sucursal;
import bo.org.citior.sife.dominio.dto.SucursalRespuesta;
import bo.org.citior.sife.dominio.dto.SucursalSolicitud;
import org.springframework.stereotype.Component;

@Component
public class SucursalMapeador {

    public Sucursal aModelo(SucursalSolicitud solicitud) {
        return Sucursal.builder()
                .codigo(solicitud.codigo().trim())
                .sucursal(solicitud.sucursal().trim())
                .estado(Boolean.TRUE.equals(solicitud.estado()))
                .build();
    }

    public void actualizar(Sucursal sucursal, SucursalSolicitud solicitud) {
        sucursal.setCodigo(solicitud.codigo().trim());
        sucursal.setSucursal(solicitud.sucursal().trim());
        sucursal.setEstado(Boolean.TRUE.equals(solicitud.estado()));
    }

    public SucursalRespuesta aRespuesta(Sucursal sucursal) {
        return new SucursalRespuesta(
                sucursal.getId(),
                sucursal.getCodigo(),
                sucursal.getSucursal(),
                sucursal.isEstado());
    }
}
