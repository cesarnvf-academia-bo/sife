import { PaginaRespuesta } from './puntoventa';

export interface MensajeSiat {
  codigo: number | null;
  descripcion: string | null;
  advertencia: boolean | null;
  numeroArchivo: number | null;
  numeroDetalle: number | null;
}

export interface FacturaDetalle {
  id: number;
  linea: number;
  actividadEconomica: string;
  codigoProductoSin: number;
  codigoProducto: string;
  descripcion: string;
  cantidad: number;
  unidadMedida: number;
  precioUnitario: number;
  montoDescuento: number | null;
  subTotal: number;
  numeroSerie: string | null;
  numeroImei: string | null;
}

export interface Factura {
  id: number;
  numeroFactura: number;
  cuf: string;
  estado: 'EMITIDA' | 'ANULADA' | 'REVERTIDA' | 'RECHAZADA';
  clienteCodigoInterno: string;
  clienteNombreRazonSocial: string;
  fechaEmision: string;
  montoTotal: number;
  respuestaCodigoEstado: number | null;
  respuestaCodigoDescripcion: string | null;
  respuestaCodigoRecepcion: string | null;
  respuestaTransaccion: boolean | null;
  respuestaMensajes: MensajeSiat[];
  detalle: FacturaDetalle[];
}

export interface FacturaPagina extends PaginaRespuesta<Factura> {}

export interface CatalogoOpcion {
  codigo: string;
  descripcion: string;
}

export interface CatalogoFactura {
  sucursales: CatalogoOpcion[];
  puntosVenta: CatalogoOpcion[];
  clientes: CatalogoOpcion[];
  tiposDocumentoIdentidad: CatalogoOpcion[];
  metodosPago: CatalogoOpcion[];
  monedas: CatalogoOpcion[];
  motivosAnulacion: CatalogoOpcion[];
}

export interface FacturaDetalleSolicitud {
  actividadEconomica: string;
  codigoProductoSin: number | null;
  codigoProducto: string;
  descripcion: string;
  cantidad: number | null;
  unidadMedida: number | null;
  precioUnitario: number | null;
  montoDescuento: number | null;
  subTotal: number | null;
  numeroSerie: string | null;
  numeroImei: string | null;
}

export interface FacturaSolicitud {
  sucursalId: number | null;
  puntoventaId: number | null;
  clienteId: number | null;
  codigoAmbiente: number | null;
  codigoEmision: number | null;
  codigoModalidad: number | null;
  codigoMetodoPago: number | null;
  tipoFacturaDocumento: number | null;
  fechaEmision: string;
  codigoTipoDocumentoIdentidad: number | null;
  nombreRazonSocial: string;
  numeroDocumento: string;
  complemento: string | null;
  municipio: string;
  direccion: string;
  telefono: string | null;
  numeroTarjeta: string | null;
  montoGiftCard: number | null;
  descuentoAdicional: number | null;
  codigoExcepcion: number | null;
  cafc: string | null;
  codigoMoneda: number | null;
  tipoCambio: number;
  leyenda: string;
  usuario: string;
  detalle: FacturaDetalleSolicitud[];
}

export interface FacturaAnulacionSolicitud {
  codigoMotivo: number;
}
