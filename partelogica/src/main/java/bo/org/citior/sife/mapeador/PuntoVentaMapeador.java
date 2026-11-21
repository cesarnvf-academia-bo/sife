package bo.org.citior.sife.mapeador;

import bo.org.citior.sife.dominio.PuntoVenta;
import bo.org.citior.sife.dominio.Sucursal;
import bo.org.citior.sife.dominio.dto.PuntoVentaRespuesta;
import bo.org.citior.sife.dominio.dto.PuntoVentaSolicitud;
import bo.org.citior.sife.dominio.dto.TipoPuntoVenta;
import org.springframework.stereotype.Component;

@Component
public class PuntoVentaMapeador {

    public PuntoVenta aModelo(PuntoVentaSolicitud solicitud) {
        return PuntoVenta.builder()
                .sucursalId(solicitud.sucursalId())
                .codigo(solicitud.codigo().trim())
                .puntoventa(solicitud.puntoventa().trim())
                .tipo(resolverTipo(solicitud.tipo()))
                .estado(Boolean.TRUE.equals(solicitud.estado()))
                .build();
    }

    public void actualizar(PuntoVenta puntoVenta, PuntoVentaSolicitud solicitud) {
        puntoVenta.setSucursalId(solicitud.sucursalId());
        puntoVenta.setCodigo(solicitud.codigo().trim());
        puntoVenta.setPuntoventa(solicitud.puntoventa().trim());
        puntoVenta.setTipo(resolverTipo(solicitud.tipo()));
        puntoVenta.setEstado(Boolean.TRUE.equals(solicitud.estado()));
    }

    public PuntoVentaRespuesta aRespuesta(PuntoVenta puntoVenta, Sucursal sucursal) {
        String codigoSucursal = sucursal == null ? null : sucursal.getCodigo();
        return new PuntoVentaRespuesta(
                puntoVenta.getId(),
                puntoVenta.getSucursalId(),
                puntoVenta.getCodigo(),
                codigoSucursal,
                puntoVenta.getPuntoventa(),
                puntoVenta.getTipo(),
                TipoPuntoVenta.desde(puntoVenta.getTipo()).getDescripcion(),
                puntoVenta.isEstado());
    }

    private int resolverTipo(Integer tipo) {
        return tipo == null ? TipoPuntoVenta.porDefecto().getCodigo() : tipo;
    }
}
