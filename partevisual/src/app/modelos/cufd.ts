export interface MensajeCufd {
  codigo: number;
  descripcion: string;
}

export interface CufdRespuesta {
  empresaId: number;
  nit: string;
  codigoSucursal: number;
  codigoPuntoVenta: number;
  cufd: string;
  codigoControl: string | null;
  direccion: string | null;
  fechaVigencia: string | null;
  transaccion: boolean;
  mensajes: MensajeCufd[];
}
