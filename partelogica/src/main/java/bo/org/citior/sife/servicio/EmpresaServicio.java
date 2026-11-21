package bo.org.citior.sife.servicio;

import bo.org.citior.sife.dominio.Empresa;
import bo.org.citior.sife.dominio.dto.EmpresaRespuesta;
import bo.org.citior.sife.dominio.dto.EmpresaSolicitud;
import bo.org.citior.sife.dominio.dto.ResultadoOperacionEmpresa;
import bo.org.citior.sife.excepcion.EmpresaNoEncontradaExcepcion;
import bo.org.citior.sife.mapeador.EmpresaMapeador;
import bo.org.citior.sife.mapper.EmpresaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmpresaServicio {

    public static final Long EMPRESA_ID = 1L;

    private final EmpresaMapper empresaMapper;
    private final EmpresaMapeador empresaMapeador;

    @Transactional
    public ResultadoOperacionEmpresa adicionar(EmpresaSolicitud solicitud) {
        Empresa existente = empresaMapper.findById(EMPRESA_ID);
        if (existente != null) {
            return new ResultadoOperacionEmpresa(modificar(EMPRESA_ID, solicitud), false);
        }

        Empresa empresa = empresaMapeador.aModelo(solicitud);
        empresa.setId(EMPRESA_ID);
        empresaMapper.insert(empresa);
        return new ResultadoOperacionEmpresa(empresaMapeador.aRespuesta(empresa), true);
    }

    @Transactional
    public EmpresaRespuesta modificar(Long id, EmpresaSolicitud solicitud) {
        Empresa empresa = empresaMapper.findById(id);
        if (empresa == null) {
            throw new EmpresaNoEncontradaExcepcion(id);
        }

        empresaMapeador.actualizar(empresa, solicitud);
        empresaMapper.update(empresa);
        return empresaMapeador.aRespuesta(empresa);
    }

    @Transactional(readOnly = true)
    public EmpresaRespuesta obtenerPorId(Long id) {
        Empresa empresa = empresaMapper.findById(id);
        if (empresa == null) {
            throw new EmpresaNoEncontradaExcepcion(id);
        }
        return empresaMapeador.aRespuesta(empresa);
    }
}
