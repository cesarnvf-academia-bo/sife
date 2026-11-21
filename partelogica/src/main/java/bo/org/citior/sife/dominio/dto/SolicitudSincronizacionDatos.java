package bo.org.citior.sife.dominio.dto;

import bo.gob.impuesto.siat.sincronizacion.SolicitudSincronizacion;
import bo.org.citior.sife.dominio.Empresa;

public record SolicitudSincronizacionDatos(
        Empresa empresa,
        SolicitudSincronizacion solicitud) {
}
