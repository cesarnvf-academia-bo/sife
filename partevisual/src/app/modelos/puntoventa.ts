export interface PuntoVenta {
  id: number;
  sucursalId: number;
  codigo: string;
  codigoSucursal: string;
  puntoventa: string;
  tipo: number;
  tipoDescripcion: string;
  estado: boolean;
}

export interface PuntoVentaSolicitud {
  sucursalId: number;
  codigo: string;
  puntoventa: string;
  tipo: number;
  estado: boolean;
}

export interface PaginaRespuesta<T> {
  content: T[];
  totalElements: number;
  totalPages: number;
  pageNumber: number;
  pageSize: number;
}

export interface TipoPuntoVenta {
  codigo: number;
  descripcion: string;
}

export const TIPOS_PUNTO_VENTA: readonly TipoPuntoVenta[] = [
  { codigo: 1, descripcion: 'Comisionista' },
  { codigo: 2, descripcion: 'Ventanilla de Cobranza' },
  { codigo: 3, descripcion: 'Móviles' },
  { codigo: 4, descripcion: 'YPFB' },
  { codigo: 5, descripcion: 'Cajeros' },
  { codigo: 6, descripcion: 'Conjunta' },
];
