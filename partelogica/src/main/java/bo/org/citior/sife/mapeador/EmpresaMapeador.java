package bo.org.citior.sife.mapeador;

import bo.org.citior.sife.dominio.Empresa;
import bo.org.citior.sife.dominio.dto.EmpresaRespuesta;
import bo.org.citior.sife.dominio.dto.EmpresaSolicitud;
import org.springframework.stereotype.Component;

@Component
public class EmpresaMapeador {

    public Empresa aEntidad(EmpresaSolicitud solicitud) {
        return Empresa.builder()
                .nit(solicitud.nit().trim())
                .razonsocial(solicitud.razonsocial().trim())
                .codigoSistema(normalizar(solicitud.codigoSistema()))
                .modalidad(normalizar(solicitud.modalidad()))
                .ambiente(normalizar(solicitud.ambiente()))
                .rutaCodigo(normalizar(solicitud.rutaCodigo()))
                .rutaSincronizacion(normalizar(solicitud.rutaSincronizacion()))
                .rutaOperacion(normalizar(solicitud.rutaOperacion()))
                .rutaCompraventa(normalizar(solicitud.rutaCompraventa()))
                .token(normalizar(solicitud.token()))
                .rutaFirmaDigital(normalizar(solicitud.rutaFirmaDigital()))
                .claveFirmaDigital(normalizar(solicitud.claveFirmaDigital()))
                .build();
    }

    public void actualizar(Empresa empresa, EmpresaSolicitud solicitud) {
        empresa.setNit(solicitud.nit().trim());
        empresa.setRazonsocial(solicitud.razonsocial().trim());
        empresa.setCodigoSistema(normalizar(solicitud.codigoSistema()));
        empresa.setModalidad(normalizar(solicitud.modalidad()));
        empresa.setAmbiente(normalizar(solicitud.ambiente()));
        empresa.setRutaCodigo(normalizar(solicitud.rutaCodigo()));
        empresa.setRutaSincronizacion(normalizar(solicitud.rutaSincronizacion()));
        empresa.setRutaOperacion(normalizar(solicitud.rutaOperacion()));
        empresa.setRutaCompraventa(normalizar(solicitud.rutaCompraventa()));
        empresa.setRutaFirmaDigital(normalizar(solicitud.rutaFirmaDigital()));

        if (tieneTexto(solicitud.token())) {
            empresa.setToken(solicitud.token().trim());
        }
        if (tieneTexto(solicitud.claveFirmaDigital())) {
            empresa.setClaveFirmaDigital(solicitud.claveFirmaDigital().trim());
        }
    }

    public EmpresaRespuesta aRespuesta(Empresa empresa) {
        return new EmpresaRespuesta(
                empresa.getId(),
                empresa.getNit(),
                empresa.getRazonsocial(),
                empresa.getCodigoSistema(),
                empresa.getModalidad(),
                empresa.getAmbiente(),
                empresa.getRutaCodigo(),
                empresa.getRutaSincronizacion(),
                empresa.getRutaOperacion(),
                empresa.getRutaCompraventa(),
                empresa.getRutaFirmaDigital(),
                tieneTexto(empresa.getToken()),
                tieneTexto(empresa.getClaveFirmaDigital()));
    }

    private boolean tieneTexto(String valor) {
        return valor != null && !valor.isBlank();
    }

    private String normalizar(String valor) {
        return tieneTexto(valor) ? valor.trim() : null;
    }
}
