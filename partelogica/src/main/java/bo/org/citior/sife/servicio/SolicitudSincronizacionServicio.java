package bo.org.citior.sife.servicio;

import bo.gob.impuesto.siat.sincronizacion.ObjectFactory;
import bo.gob.impuesto.siat.sincronizacion.SolicitudSincronizacion;
import bo.org.citior.sife.dominio.Empresa;
import bo.org.citior.sife.dominio.PuntoVenta;
import bo.org.citior.sife.dominio.Sucursal;
import bo.org.citior.sife.dominio.dto.SolicitudSincronizacionDatos;
import bo.org.citior.sife.excepcion.ConfiguracionSiatInvalidaExcepcion;
import bo.org.citior.sife.excepcion.EmpresaNoEncontradaExcepcion;
import bo.org.citior.sife.excepcion.SiatNoDisponibleExcepcion;
import bo.org.citior.sife.excepcion.TokenSiatNoConfiguradoExcepcion;
import bo.org.citior.sife.mapper.EmpresaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SolicitudSincronizacionServicio {

    private final EmpresaMapper empresaMapper;

    @Transactional(readOnly = true)
    public SolicitudSincronizacionDatos construir(
            Sucursal sucursal, PuntoVenta puntoVenta, String cuis) {
        Empresa empresa = empresaMapper.findById(EmpresaServicio.EMPRESA_ID);
        if (empresa == null) {
            throw new EmpresaNoEncontradaExcepcion(EmpresaServicio.EMPRESA_ID);
        }
        if (empresa.getToken() == null || empresa.getToken().isBlank()) {
            throw new TokenSiatNoConfiguradoExcepcion(empresa.getId());
        }

        String codigoSistema = empresa.getCodigoSistema();
        if (codigoSistema == null || codigoSistema.isBlank()) {
            throw new ConfiguracionSiatInvalidaExcepcion(empresa.getId(), "codigoSistema");
        }

        int codigoAmbiente = parsearEntero(empresa, "ambiente", empresa.getAmbiente());
        long nit = parsearLong(empresa, "nit", empresa.getNit());
        int codigoSucursal = parsearEntero(empresa, "sucursal.codigo", sucursal.getCodigo());
        int codigoPuntoVenta = parsearEntero(
                empresa, "puntoventa.codigo", puntoVenta.getCodigo());

        if (cuis == null || cuis.isBlank()) {
            throw new SiatNoDisponibleExcepcion(
                    "FacturacionSincronizacion", "CUIS no disponible");
        }

        SolicitudSincronizacion solicitud = new SolicitudSincronizacion();
        solicitud.setCodigoAmbiente(codigoAmbiente);
        solicitud.setCodigoSistema(codigoSistema.trim());
        solicitud.setCodigoSucursal(codigoSucursal);
        solicitud.setNit(nit);
        solicitud.setCuis(cuis.trim());
        solicitud.setCodigoPuntoVenta(new ObjectFactory()
                .createSolicitudSincronizacionCodigoPuntoVenta(codigoPuntoVenta));

        return new SolicitudSincronizacionDatos(empresa, solicitud);
    }

    private int parsearEntero(Empresa empresa, String campo, String valor) {
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException | NullPointerException excepcion) {
            throw new ConfiguracionSiatInvalidaExcepcion(empresa.getId(), campo);
        }
    }

    private long parsearLong(Empresa empresa, String campo, String valor) {
        try {
            return Long.parseLong(valor.trim());
        } catch (NumberFormatException | NullPointerException excepcion) {
            throw new ConfiguracionSiatInvalidaExcepcion(empresa.getId(), campo);
        }
    }
}
