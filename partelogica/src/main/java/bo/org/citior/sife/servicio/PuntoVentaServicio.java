package bo.org.citior.sife.servicio;

import bo.org.citior.sife.dominio.PuntoVenta;
import bo.org.citior.sife.dominio.Sucursal;
import bo.org.citior.sife.dominio.dto.CufdRespuesta;
import bo.org.citior.sife.dominio.dto.CuisRespuesta;
import bo.org.citior.sife.dominio.dto.PaginaRespuesta;
import bo.org.citior.sife.dominio.dto.PuntoVentaRespuesta;
import bo.org.citior.sife.dominio.dto.PuntoVentaSolicitud;
import bo.org.citior.sife.excepcion.ConfiguracionSiatInvalidaExcepcion;
import bo.org.citior.sife.excepcion.PuntoVentaCodigoDuplicadoExcepcion;
import bo.org.citior.sife.excepcion.PuntoVentaNoEncontradoExcepcion;
import bo.org.citior.sife.excepcion.SucursalNoEncontradaExcepcion;
import bo.org.citior.sife.mapeador.PuntoVentaMapeador;
import bo.org.citior.sife.mapper.PuntoVentaMapper;
import bo.org.citior.sife.mapper.SucursalMapper;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PuntoVentaServicio {

    private static final Set<Integer> TAMANOS_PERMITIDOS = Set.of(5, 10, 15, 20);

    private final PuntoVentaMapper puntoVentaMapper;
    private final SucursalMapper sucursalMapper;
    private final PuntoVentaMapeador puntoVentaMapeador;
    private final ObtenerCuisServicio obtenerCuisServicio;
    private final ObtenerCufdServicio obtenerCufdServicio;
    private final RegistroCuisServicio registroCuisServicio;
    private final RegistroCufdServicio registroCufdServicio;

    @Transactional(readOnly = true)
    public PaginaRespuesta<PuntoVentaRespuesta> listar(String busqueda, int page, int size) {
        validarTamano(size);

        String filtro = normalizar(busqueda);
        int pagina = Math.max(page, 0);
        long totalElements = puntoVentaMapper.contar(filtro);
        List<PuntoVentaRespuesta> contenido = puntoVentaMapper
                .listar(filtro, pagina * size, size).stream()
                .map(puntoVenta -> puntoVentaMapeador.aRespuesta(
                        puntoVenta, sucursalMapper.findById(puntoVenta.getSucursalId())))
                .toList();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new PaginaRespuesta<>(
                contenido, totalElements, totalPages, pagina, size);
    }

    @Transactional(readOnly = true)
    public List<PuntoVenta> listarActivos() {
        return puntoVentaMapper.listarActivos();
    }

    @Transactional(readOnly = true)
    public PuntoVentaRespuesta obtenerPorId(Long id) {
        PuntoVenta puntoVenta = obtenerModelo(id);
        return puntoVentaMapeador.aRespuesta(
                puntoVenta, sucursalMapper.findById(puntoVenta.getSucursalId()));
    }

    @Transactional
    public PuntoVentaRespuesta adicionar(PuntoVentaSolicitud solicitud) {
        validarSucursal(solicitud.sucursalId());
        validarCodigoDisponible(solicitud.sucursalId(), solicitud.codigo().trim(), null);

        PuntoVenta puntoVenta = puntoVentaMapeador.aModelo(solicitud);
        puntoVentaMapper.insert(puntoVenta);
        return puntoVentaMapeador.aRespuesta(
                puntoVenta, sucursalMapper.findById(puntoVenta.getSucursalId()));
    }

    @Transactional
    public PuntoVentaRespuesta modificar(Long id, PuntoVentaSolicitud solicitud) {
        PuntoVenta puntoVenta = obtenerModelo(id);
        validarSucursal(solicitud.sucursalId());
        validarCodigoDisponible(solicitud.sucursalId(), solicitud.codigo().trim(), id);

        puntoVentaMapeador.actualizar(puntoVenta, solicitud);
        puntoVentaMapper.update(puntoVenta);
        return puntoVentaMapeador.aRespuesta(
                puntoVenta, sucursalMapper.findById(puntoVenta.getSucursalId()));
    }

    @Transactional
    public void eliminar(Long id) {
        obtenerModelo(id);
        puntoVentaMapper.delete(id);
    }

    @Transactional(readOnly = true)
    public PuntoVenta obtenerModelo(Long id) {
        PuntoVenta puntoVenta = puntoVentaMapper.findById(id);
        if (puntoVenta == null) {
            throw new PuntoVentaNoEncontradoExcepcion(id);
        }
        return puntoVenta;
    }

    @Transactional
    public CuisRespuesta obtenerCuis(Long id) {
        PuntoVenta puntoVenta = obtenerModelo(id);
        int[] codigos = codigosSiat(puntoVenta);

        CuisRespuesta respuesta = obtenerCuisServicio.obtener(
                EmpresaServicio.EMPRESA_ID, codigos[0], codigos[1]);
        registroCuisServicio.registrar(respuesta, puntoVenta);
        return respuesta;
    }

    @Transactional
    public CufdRespuesta obtenerCufd(Long id) {
        PuntoVenta puntoVenta = obtenerModelo(id);
        int[] codigos = codigosSiat(puntoVenta);

        CuisRespuesta cuisRespuesta = obtenerCuisServicio.obtener(
                EmpresaServicio.EMPRESA_ID, codigos[0], codigos[1]);
        Long cuisId = registroCuisServicio.registrar(cuisRespuesta, puntoVenta);

        CufdRespuesta respuesta = obtenerCufdServicio.obtenerConCuis(
                EmpresaServicio.EMPRESA_ID, codigos[0], codigos[1], cuisRespuesta);
        registroCufdServicio.registrar(respuesta, puntoVenta, cuisId);
        return respuesta;
    }

    private int[] codigosSiat(PuntoVenta puntoVenta) {
        Sucursal sucursal = sucursalMapper.findById(puntoVenta.getSucursalId());
        if (sucursal == null) {
            throw new SucursalNoEncontradaExcepcion(puntoVenta.getSucursalId());
        }
        return new int[]{
                parsearCodigo(sucursal.getCodigo(), "sucursal.codigo"),
                parsearCodigo(puntoVenta.getCodigo(), "puntoventa.codigo")
        };
    }

    private int parsearCodigo(String valor, String campo) {
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException | NullPointerException excepcion) {
            throw new ConfiguracionSiatInvalidaExcepcion(
                    EmpresaServicio.EMPRESA_ID, campo);
        }
    }

    private void validarSucursal(Long sucursalId) {
        if (sucursalMapper.findById(sucursalId) == null) {
            throw new SucursalNoEncontradaExcepcion(sucursalId);
        }
    }

    private void validarCodigoDisponible(Long sucursalId, String codigo, Long id) {
        boolean duplicado = id == null
                ? puntoVentaMapper.existsBySucursalIdAndCodigo(sucursalId, codigo)
                : puntoVentaMapper.existsBySucursalIdAndCodigoAndIdNot(sucursalId, codigo, id);
        if (duplicado) {
            throw new PuntoVentaCodigoDuplicadoExcepcion(codigo);
        }
    }

    private void validarTamano(int size) {
        if (!TAMANOS_PERMITIDOS.contains(size)) {
            throw new IllegalArgumentException("El tamaño debe ser 5, 10, 15 o 20.");
        }
    }

    private String normalizar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }
}
