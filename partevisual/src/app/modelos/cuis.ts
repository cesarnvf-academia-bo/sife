export interface MensajeCuis {
  codigo: number;
  descripcion: string;
}

export interface CuisRespuesta {
  empresaId: number;
  nit: string;
  codigoSucursal: number;
  codigoPuntoVenta: number;
  cuis: string;
  fechaVigencia: string | null;
  transaccion: boolean;
  mensajes: MensajeCuis[];
}
