export interface Sucursal {
  id: number;
  codigo: string;
  sucursal: string;
  estado: boolean;
}

export interface SucursalSolicitud {
  codigo: string;
  sucursal: string;
  estado: boolean;
}

export interface SucursalPagina {
  content: Sucursal[];
  totalElements: number;
  totalPages: number;
  pageNumber: number;
  pageSize: number;
}
