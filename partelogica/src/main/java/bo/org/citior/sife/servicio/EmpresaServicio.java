package bo.org.citior.sife.servicio;

import bo.org.citior.sife.dominio.Empresa;
import bo.org.citior.sife.dominio.dto.EmpresaRespuesta;
import bo.org.citior.sife.dominio.dto.EmpresaSolicitud;
import bo.org.citior.sife.excepcion.EmpresaNoEncontradaExcepcion;
import bo.org.citior.sife.mapeador.EmpresaMapeador;
import bo.org.citior.sife.repositorio.EmpresaRepositorio;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmpresaServicio {

    private final EmpresaRepositorio empresaRepositorio;
    private final EmpresaMapeador empresaMapeador;

    @Transactional
    public EmpresaRespuesta adicionar(EmpresaSolicitud solicitud) {
        Empresa empresa = empresaMapeador.aEntidad(solicitud);
        return empresaMapeador.aRespuesta(empresaRepositorio.save(empresa));
    }

    @Transactional
    public EmpresaRespuesta modificar(Long id, EmpresaSolicitud solicitud) {
        Empresa empresa = empresaRepositorio.findById(id)
                .orElseThrow(() -> new EmpresaNoEncontradaExcepcion(id));
        empresaMapeador.actualizar(empresa, solicitud);
        return empresaMapeador.aRespuesta(empresaRepositorio.save(empresa));
    }

    @Transactional(readOnly = true)
    public EmpresaRespuesta obtenerPorId(Long id) {
        return empresaRepositorio.findById(id)
                .map(empresaMapeador::aRespuesta)
                .orElseThrow(() -> new EmpresaNoEncontradaExcepcion(id));
    }
}
