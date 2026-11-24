package bo.org.citior.sife.dominio;

import java.math.BigDecimal;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Factura {

    private Long id;
    private Long empresaId;
    private Long sucursalId;
    private Long puntoventaId;
    private Long cuisId;
    private Long cufdId;
    private long nitEmisor;
    private String razonSocialEmisor;
    private String municipio;
    private String telefono;
    private long numeroFactura;
    private String cuf;
    private String cufd;
    private int codigoSucursal;
    private String direccion;
    private Integer codigoPuntoVenta;
    private Instant fechaEmision;
    private String nombreRazonSocial;
    private int codigoTipoDocumentoIdentidad;
    private String numeroDocumento;
    private String complemento;
    private String codigoCliente;
    private int codigoMetodoPago;
    private String numeroTarjeta;
    private BigDecimal montoTotal;
    private BigDecimal montoTotalSujetoIva;
    private BigDecimal montoGiftCard;
    private BigDecimal descuentoAdicional;
    private Integer codigoExcepcion;
    private String cafc;
    private int codigoMoneda;
    private BigDecimal tipoCambio;
    private BigDecimal montoTotalMoneda;
    private String leyenda;
    private String usuario;
    private int codigoDocumentoSector;
    private int codigoAmbiente;
    private int codigoEmision;
    private int codigoModalidad;
    private int tipoFacturaDocumento;
    private String xmlFirmado;
    private String hashArchivo;
    private Instant fechaEnvio;
    private byte[] archivoGzip;
    private String respuestaCodigoDescripcion;
    private String respuestaCodigoRecepcion;
    private String respuestaError;
    private String estado;
    private String anulacionCodigoDescripcion;
    private String reversionCodigoDescripcion;
    private Integer respuestaCodigoEstado;
    private Integer anulacionCodigoEstado;
    private Integer reversionCodigoEstado;
    private Boolean respuestaTransaccion;
    private Boolean anulacionTransaccion;
    private Boolean reversionTransaccion;
    private String respuestaMensajes;
    private String anulacionMensajes;
    private String reversionMensajes;
    private Integer codigoMotivoAnulacion;
    private Instant fechaAnulacion;
    private Instant fechaReversion;
    private Instant creadoEn;
    private Instant actualizadoEn;
}
