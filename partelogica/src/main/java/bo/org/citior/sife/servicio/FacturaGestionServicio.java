package bo.org.citior.sife.servicio;

import bo.org.citior.sife.configuracion.PropiedadesCorreo;
import bo.org.citior.sife.dominio.Cliente;
import bo.org.citior.sife.dominio.Empresa;
import bo.org.citior.sife.dominio.Factura;
import bo.org.citior.sife.dominio.FacturaDetalle;
import bo.org.citior.sife.dominio.PuntoVenta;
import bo.org.citior.sife.dominio.Sucursal;
import bo.org.citior.sife.dominio.Cufd;
import bo.org.citior.sife.dominio.Cuis;
import bo.org.citior.sife.dominio.dto.FacturaDetalleSolicitud;
import bo.org.citior.sife.dominio.dto.FacturaRespuesta;
import bo.org.citior.sife.dominio.dto.FacturaSolicitud;
import bo.org.citior.sife.dominio.dto.PaginaRespuesta;
import bo.org.citior.sife.dominio.dto.RespuestaSiat;
import bo.org.citior.sife.evento.FacturaAprobadaEvento;
import bo.org.citior.sife.evento.FacturaCorreoProductor;
import bo.org.citior.sife.excepcion.CatalogoProductoNoValidoExcepcion;
import bo.org.citior.sife.excepcion.ClienteNoEncontradoExcepcion;
import bo.org.citior.sife.excepcion.ConfiguracionSiatInvalidaExcepcion;
import bo.org.citior.sife.excepcion.EmpresaNoEncontradaExcepcion;
import bo.org.citior.sife.excepcion.FacturaNoEncontradaExcepcion;
import bo.org.citior.sife.excepcion.PuntoVentaNoEncontradoExcepcion;
import bo.org.citior.sife.excepcion.SucursalNoEncontradaExcepcion;
import bo.org.citior.sife.excepcion.TokenSiatNoConfiguradoExcepcion;
import bo.org.citior.sife.excepcion.XmlFacturaInvalidoExcepcion;
import bo.org.citior.sife.mapeador.FacturaMapeador;
import bo.org.citior.sife.mapper.ClienteMapper;
import bo.org.citior.sife.mapper.CufdMapper;
import bo.org.citior.sife.mapper.CuisMapper;
import bo.org.citior.sife.mapper.EmpresaMapper;
import bo.org.citior.sife.mapper.FacturaDetalleMapper;
import bo.org.citior.sife.mapper.FacturaMapper;
import bo.org.citior.sife.mapper.PuntoVentaMapper;
import bo.org.citior.sife.mapper.SucursalMapper;
import bo.org.citior.sife.servicio.ComprimirFacturaServicio.ArchivoComprimido;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;
import org.w3c.dom.Document;

@Slf4j
@Service
@RequiredArgsConstructor
public class FacturaGestionServicio {

    private static final ZoneId ZONA_SIAT = ZoneId.of("America/La_Paz");

    private static final Set<Integer> TAMANOS_PERMITIDOS = Set.of(5, 10, 15, 20);
    private static final String ESTADO_EMITIDA = "EMITIDA";
    private static final String ESTADO_RECHAZADA = "RECHAZADA";
    private static final int CODIGO_DOCUMENTO_SECTOR = 1;

    private final FacturaMapper facturaMapper;
    private final FacturaDetalleMapper facturaDetalleMapper;
    private final FacturaMapeador facturaMapeador;
    private final EmpresaMapper empresaMapper;
    private final SucursalMapper sucursalMapper;
    private final PuntoVentaMapper puntoVentaMapper;
    private final ClienteMapper clienteMapper;
    private final CuisMapper cuisMapper;
    private final CufdMapper cufdMapper;
    private final GenerarCufServicio generarCufServicio;
    private final GenerarXmlFacturaServicio generarXmlFacturaServicio;
    private final FirmarXmlServicio firmarXmlServicio;
    private final ComprimirFacturaServicio comprimirFacturaServicio;
    private final FacturaSiatServicio facturaSiatServicio;
    private final CatalogoProductoServicio catalogoProductoServicio;
    private final FacturaCorreoProductor facturaCorreoProductor;
    private final PropiedadesCorreo propiedadesCorreo;

    @Transactional(readOnly = true)
    public PaginaRespuesta<FacturaRespuesta> listar(String busqueda, int page, int size) {
        validarTamano(size);

        String filtro = normalizar(busqueda);
        int pagina = Math.max(page, 0);
        long totalElements = facturaMapper.contar(filtro);
        List<FacturaRespuesta> contenido = facturaMapper
                .listar(filtro, pagina * size, size).stream()
                .map(this::aRespuesta)
                .toList();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return new PaginaRespuesta<>(
                contenido, totalElements, totalPages, pagina, size);
    }

    @Transactional(readOnly = true)
    public FacturaRespuesta obtenerPorId(Long id) {
        return aRespuesta(obtenerFactura(id));
    }

    @Transactional(readOnly = true)
    public byte[] obtenerXml(Long id) {
        Factura factura = obtenerFactura(id);
        if (factura.getXmlFirmado() == null || factura.getXmlFirmado().isBlank()) {
            throw new XmlFacturaInvalidoExcepcion(
                    "La factura con id " + id + " no tiene XML firmado.");
        }
        return factura.getXmlFirmado().getBytes(StandardCharsets.UTF_8);
    }

    @Transactional
    public FacturaRespuesta emitir(FacturaSolicitud solicitud) {
        if (solicitud == null) {
            throw new IllegalArgumentException("solicitud es obligatoria.");
        }
        validarLineas(solicitud.detalle());

        Empresa empresa = empresaMapper.findById(EmpresaServicio.EMPRESA_ID);
        if (empresa == null) {
            throw new EmpresaNoEncontradaExcepcion(EmpresaServicio.EMPRESA_ID);
        }
        if (empresa.getToken() == null || empresa.getToken().isBlank()) {
            throw new TokenSiatNoConfiguradoExcepcion(empresa.getId());
        }
        if (empresa.getCodigoSistema() == null || empresa.getCodigoSistema().isBlank()) {
            throw new ConfiguracionSiatInvalidaExcepcion(empresa.getId(), "codigoSistema");
        }
        if (empresa.getRazonsocial() == null || empresa.getRazonsocial().isBlank()) {
            throw new ConfiguracionSiatInvalidaExcepcion(empresa.getId(), "razonsocial");
        }

        if (solicitud.sucursalId() == null) {
            throw new IllegalArgumentException("sucursalId es obligatorio.");
        }
        Sucursal sucursal = sucursalMapper.findById(solicitud.sucursalId());
        if (sucursal == null) {
            throw new SucursalNoEncontradaExcepcion(solicitud.sucursalId());
        }
        if (solicitud.puntoventaId() == null) {
            throw new IllegalArgumentException("puntoventaId es obligatorio.");
        }
        PuntoVenta puntoVenta = puntoVentaMapper.findById(solicitud.puntoventaId());
        if (puntoVenta == null) {
            throw new PuntoVentaNoEncontradoExcepcion(solicitud.puntoventaId());
        }
        if (solicitud.clienteId() == null) {
            throw new IllegalArgumentException("clienteId es obligatorio.");
        }
        Cliente cliente = clienteMapper.findById(solicitud.clienteId());
        if (cliente == null) {
            throw new ClienteNoEncontradoExcepcion(solicitud.clienteId());
        }

        Cuis cuis = cuisMapper.obtenerVigente(empresa.getId(), puntoVenta.getId());
        if (cuis == null || esVacio(cuis.getCuis())) {
            throw new ConfiguracionSiatInvalidaExcepcion(empresa.getId(), "cuis");
        }
        Cufd cufd = cufdMapper.obtenerVigente(empresa.getId(), puntoVenta.getId());
        if (cufd == null || esVacio(cufd.getCufd()) || esVacio(cufd.getCodigoControl())) {
            throw new ConfiguracionSiatInvalidaExcepcion(empresa.getId(), "cufd");
        }

        validarCatalogos(solicitud.detalle());

        Instant fechaEmision = parsearFecha(solicitud.fechaEmision());
        BigDecimal descuentoAdicional = solicitud.descuentoAdicional() == null
                ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                : solicitud.descuentoAdicional();
        BigDecimal tipoCambio = requeridoDecimal(solicitud.tipoCambio(), "tipoCambio");
        if (tipoCambio.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("tipoCambio debe ser mayor a cero.");
        }

        List<FacturaDetalle> detalles = construirDetalles(solicitud.detalle());
        BigDecimal montoTotal = detalles.stream()
                .map(FacturaDetalle::getSubTotal)
                .reduce(BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP), BigDecimal::add)
                .subtract(descuentoAdicional)
                .setScale(2, RoundingMode.HALF_UP);
        if (montoTotal.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("montoTotal debe ser mayor a cero.");
        }
        BigDecimal montoTotalMoneda = montoTotal.divide(tipoCambio, 2, RoundingMode.HALF_UP);

        long numeroFactura = facturaMapper.siguienteNumeroFactura();
        long nitEmisor = parsearNit(empresa);
        int codigoSucursal = parsearCodigo(sucursal.getCodigo(), "sucursal.codigo", empresa);
        int codigoPuntoVenta = parsearCodigo(puntoVenta.getCodigo(), "puntoventa.codigo", empresa);
        String cuf = generarCufServicio.generar(
                nitEmisor,
                fechaEmision,
                codigoSucursal,
                requerido(solicitud.codigoModalidad(), "codigoModalidad"),
                requerido(solicitud.codigoEmision(), "codigoEmision"),
                requerido(solicitud.tipoFacturaDocumento(), "tipoFacturaDocumento"),
                numeroFactura,
                codigoPuntoVenta,
                cufd.getCodigoControl());

        String direccion = valorO(solicitud.direccion(), cufd.getDireccion());
        if (esVacio(direccion)) {
            throw new ConfiguracionSiatInvalidaExcepcion(empresa.getId(), "direccion");
        }
        int codigoTipoDocumento = solicitud.codigoTipoDocumentoIdentidad() == null
                ? cliente.getTipo()
                : solicitud.codigoTipoDocumentoIdentidad();

        Factura factura = Factura.builder()
                .empresaId(empresa.getId())
                .sucursalId(sucursal.getId())
                .puntoventaId(puntoVenta.getId())
                .cuisId(cuis.getId())
                .cufdId(cufd.getId())
                .nitEmisor(nitEmisor)
                .razonSocialEmisor(empresa.getRazonsocial())
                .municipio(valorO(solicitud.municipio(), "La Paz"))
                .telefono(solicitud.telefono())
                .numeroFactura(numeroFactura)
                .cuf(cuf)
                .cufd(cufd.getCufd())
                .codigoSucursal(codigoSucursal)
                .direccion(direccion)
                .codigoPuntoVenta(codigoPuntoVenta)
                .fechaEmision(fechaEmision)
                .nombreRazonSocial(solicitud.nombreRazonSocial())
                .codigoTipoDocumentoIdentidad(codigoTipoDocumento)
                .numeroDocumento(solicitud.numeroDocumento())
                .complemento(solicitud.complemento())
                .codigoCliente(cliente.getCodigoInterno())
                .codigoMetodoPago(requerido(solicitud.codigoMetodoPago(), "codigoMetodoPago"))
                .numeroTarjeta(solicitud.numeroTarjeta())
                .montoTotal(montoTotal)
                .montoTotalSujetoIva(montoTotal)
                .montoGiftCard(solicitud.montoGiftCard())
                .descuentoAdicional(descuentoAdicional)
                .codigoExcepcion(solicitud.codigoExcepcion())
                .cafc(solicitud.cafc())
                .codigoMoneda(requerido(solicitud.codigoMoneda(), "codigoMoneda"))
                .tipoCambio(tipoCambio)
                .montoTotalMoneda(montoTotalMoneda)
                .leyenda(solicitud.leyenda())
                .usuario(solicitud.usuario())
                .codigoDocumentoSector(CODIGO_DOCUMENTO_SECTOR)
                .codigoAmbiente(requerido(solicitud.codigoAmbiente(), "codigoAmbiente"))
                .codigoEmision(requerido(solicitud.codigoEmision(), "codigoEmision"))
                .codigoModalidad(requerido(solicitud.codigoModalidad(), "codigoModalidad"))
                .tipoFacturaDocumento(
                        requerido(solicitud.tipoFacturaDocumento(), "tipoFacturaDocumento"))
                .estado(ESTADO_EMITIDA)
                .build();

        Document documento = generarXmlFacturaServicio.construir(factura, detalles);
        generarXmlFacturaServicio.validar(documento);
        byte[] xmlFirmado = firmarXmlServicio.firmar(documento, empresa);
        ArchivoComprimido comprimido = comprimirFacturaServicio.comprimir(xmlFirmado);

        Instant fechaEnvio = Instant.now();
        RespuestaSiat respuesta = facturaSiatServicio.recibir(
                factura,
                empresa,
                cuis,
                cufd,
                comprimido.gzip(),
                comprimido.hashSha256(),
                fechaEnvio);

        aplicarRespuesta(factura, respuesta, xmlFirmado, comprimido, fechaEnvio);
        facturaMapper.insertar(factura);
        facturaDetalleMapper.insertarLote(factura.getId(), detalles);
        notificarAprobacionSiCorresponde(factura, respuesta, cliente);
        return facturaMapeador.aRespuesta(factura, detalles);
    }

    public void notificarAprobacionSiCorresponde(
            Factura factura, RespuestaSiat respuesta, Cliente cliente) {
        if (!esAprobacion908(respuesta)) {
            return;
        }
        if (cliente == null || cliente.getCorreo() == null || cliente.getCorreo().isBlank()) {
            log.warn("La factura {} no tiene correo de cliente; no se publicó la notificación.",
                    factura.getId());
            return;
        }
        FacturaAprobadaEvento evento = new FacturaAprobadaEvento(
                factura.getId(),
                factura.getNumeroFactura(),
                factura.getCuf(),
                factura.getNombreRazonSocial(),
                cliente.getCorreo(),
                factura.getFechaEmision() == null
                        ? null
                        : DateTimeFormatter.ISO_INSTANT.format(factura.getFechaEmision()),
                factura.getMontoTotal(),
                factura.getRespuestaCodigoRecepcion());
        publicarDespuesDeConfirmar(evento);
    }

    private boolean esAprobacion908(RespuestaSiat respuesta) {
        if (respuesta == null || !Boolean.TRUE.equals(respuesta.transaccion())) {
            return false;
        }
        int codigo = propiedadesCorreo.codigoAprobado();
        if (respuesta.codigoEstado() != null && respuesta.codigoEstado() == codigo) {
            return true;
        }
        return respuesta.mensajes() != null && respuesta.mensajes().stream()
                .anyMatch(mensaje -> mensaje != null
                        && Integer.valueOf(codigo).equals(mensaje.codigo()));
    }

    private void publicarDespuesDeConfirmar(FacturaAprobadaEvento evento) {
        if (!TransactionSynchronizationManager.isSynchronizationActive()) {
            facturaCorreoProductor.publicar(evento);
            return;
        }
        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                facturaCorreoProductor.publicar(evento);
            }
        });
    }

    private FacturaRespuesta aRespuesta(Factura factura) {
        return facturaMapeador.aRespuesta(
                factura, facturaDetalleMapper.listarPorFacturaId(factura.getId()));
    }

    private Factura obtenerFactura(Long id) {
        Factura factura = facturaMapper.obtenerPorId(id);
        if (factura == null) {
            throw new FacturaNoEncontradaExcepcion(id);
        }
        return factura;
    }

    private void validarLineas(List<FacturaDetalleSolicitud> detalles) {
        if (detalles == null || detalles.isEmpty() || detalles.size() > 500) {
            throw new IllegalArgumentException("El detalle debe tener entre 1 y 500 líneas.");
        }
        for (int indice = 0; indice < detalles.size(); indice++) {
            if (detalles.get(indice) == null) {
                throw new IllegalArgumentException(
                        "La línea " + (indice + 1) + " del detalle es obligatoria.");
            }
        }
    }

    private void validarCatalogos(List<FacturaDetalleSolicitud> detalles) {
        for (int indice = 0; indice < detalles.size(); indice++) {
            FacturaDetalleSolicitud detalle = detalles.get(indice);
            if (!catalogoProductoServicio.existeProductoServicio(detalle.codigoProductoSin())) {
                throw new CatalogoProductoNoValidoExcepcion(
                        "codigoProductoSin", detalle.codigoProductoSin());
            }
            if (!catalogoProductoServicio.existeUnidadMedida(detalle.unidadMedida())) {
                throw new CatalogoProductoNoValidoExcepcion(
                        "unidadMedida", detalle.unidadMedida());
            }
        }
    }

    private List<FacturaDetalle> construirDetalles(List<FacturaDetalleSolicitud> solicitudes) {
        List<FacturaDetalle> detalles = new ArrayList<>();
        for (int indice = 0; indice < solicitudes.size(); indice++) {
            FacturaDetalleSolicitud solicitud = solicitudes.get(indice);
            BigDecimal descuento = solicitud.montoDescuento() == null
                    ? BigDecimal.ZERO.setScale(2, RoundingMode.HALF_UP)
                    : solicitud.montoDescuento();
            BigDecimal cantidad = requeridoDecimal(solicitud.cantidad(), "cantidad");
            BigDecimal precioUnitario =
                    requeridoDecimal(solicitud.precioUnitario(), "precioUnitario");
            BigDecimal subTotal = cantidad.multiply(precioUnitario)
                    .subtract(descuento)
                    .setScale(2, RoundingMode.HALF_UP);
            if (subTotal.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException(
                        "El subTotal de la línea " + (indice + 1) + " debe ser mayor a cero.");
            }

            detalles.add(FacturaDetalle.builder()
                    .linea(indice + 1)
                    .actividadEconomica(solicitud.actividadEconomica())
                    .codigoProductoSin(solicitud.codigoProductoSin())
                    .codigoProducto(solicitud.codigoProducto())
                    .descripcion(solicitud.descripcion())
                    .cantidad(cantidad)
                    .unidadMedida(requerido(solicitud.unidadMedida(), "unidadMedida"))
                    .precioUnitario(precioUnitario)
                    .montoDescuento(solicitud.montoDescuento())
                    .subTotal(subTotal)
                    .numeroSerie(solicitud.numeroSerie())
                    .numeroImei(solicitud.numeroImei())
                    .build());
        }
        return detalles;
    }

    private void aplicarRespuesta(
            Factura factura,
            RespuestaSiat respuesta,
            byte[] xmlFirmado,
            ArchivoComprimido comprimido,
            Instant fechaEnvio) {
        factura.setXmlFirmado(new String(xmlFirmado, StandardCharsets.UTF_8));
        factura.setHashArchivo(comprimido.hashSha256());
        factura.setArchivoGzip(comprimido.gzip());
        factura.setFechaEnvio(fechaEnvio);
        factura.setRespuestaCodigoDescripcion(respuesta.codigoDescripcion());
        factura.setRespuestaCodigoEstado(respuesta.codigoEstado());
        factura.setRespuestaCodigoRecepcion(respuesta.codigoRecepcion());
        factura.setRespuestaTransaccion(respuesta.transaccion());
        factura.setRespuestaMensajes(facturaMapeador.serializarMensajes(respuesta.mensajes()));
        factura.setEstado(
                Boolean.TRUE.equals(respuesta.transaccion()) ? ESTADO_EMITIDA : ESTADO_RECHAZADA);
    }

    private Instant parsearFecha(String texto) {
        if (esVacio(texto)) {
            throw new IllegalArgumentException("fechaEmision es obligatoria.");
        }
        try {
            return OffsetDateTime.parse(texto.trim(), DateTimeFormatter.ISO_OFFSET_DATE_TIME)
                    .toInstant();
        } catch (DateTimeParseException primera) {
            try {
                return LocalDateTime.parse(texto.trim(), DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                        .atZone(ZONA_SIAT)
                        .toInstant();
            } catch (DateTimeParseException excepcion) {
                throw new IllegalArgumentException(
                        "fechaEmision debe tener un formato ISO válido.", excepcion);
            }
        }
    }

    private long parsearNit(Empresa empresa) {
        String nit = empresa.getNit();
        if (esVacio(nit)) {
            throw new ConfiguracionSiatInvalidaExcepcion(empresa.getId(), "nit");
        }
        try {
            return Long.parseLong(nit.trim());
        } catch (NumberFormatException excepcion) {
            throw new ConfiguracionSiatInvalidaExcepcion(empresa.getId(), "nit");
        }
    }

    private int parsearCodigo(String valor, String campo, Empresa empresa) {
        if (esVacio(valor)) {
            throw new ConfiguracionSiatInvalidaExcepcion(empresa.getId(), campo);
        }
        try {
            return Integer.parseInt(valor.trim());
        } catch (NumberFormatException excepcion) {
            throw new ConfiguracionSiatInvalidaExcepcion(empresa.getId(), campo);
        }
    }

    private int requerido(Integer valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException(campo + " es obligatorio.");
        }
        return valor;
    }

    private BigDecimal requeridoDecimal(BigDecimal valor, String campo) {
        if (valor == null) {
            throw new IllegalArgumentException(campo + " es obligatorio.");
        }
        return valor;
    }

    private String valorO(String valor, String alternativo) {
        return esVacio(valor) ? alternativo : valor;
    }

    private boolean esVacio(String valor) {
        return valor == null || valor.isBlank();
    }

    private String normalizar(String valor) {
        return valor == null || valor.isBlank() ? null : valor.trim();
    }

    private void validarTamano(int size) {
        if (!TAMANOS_PERMITIDOS.contains(size)) {
            throw new IllegalArgumentException("El tamaño debe ser 5, 10, 15 o 20.");
        }
    }
}
