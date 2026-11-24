export interface Producto {
  id: number;
  codigoInterno: string;
  producto: string;
  precioUnitario: number;
  codigoActividad: string;
  actividadDescripcion: string | null;
  codigoProducto: number;
  productoDescripcion: string | null;
  unidadMedida: number;
  unidadMedidaDescripcion: string | null;
}

export interface ProductoSolicitud {
  codigoInterno: string;
  producto: string;
  precioUnitario: number;
  codigoActividad: string;
  codigoProducto: number;
  unidadMedida: number;
}

export interface ProductoPagina {
  content: Producto[];
  totalElements: number;
  totalPages: number;
  pageNumber: number;
  pageSize: number;
}

export interface ActividadOpcion {
  codigo: string;
  descripcion: string;
}

export interface ProductoServicioOpcion {
  codigoActividad: string;
  codigo: number;
  descripcion: string;
}

export interface UnidadMedidaOpcion {
  codigo: number;
  descripcion: string;
}
