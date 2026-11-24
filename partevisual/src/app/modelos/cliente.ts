export interface Cliente {
  id: number;
  codigoInterno: string;
  cliente: string;
  dit: string;
  tipo: number;
  tipoDescripcion: string | null;
  correo: string;
}

export interface ClienteSolicitud {
  codigoInterno: string;
  cliente: string;
  dit: string;
  tipo: number;
  correo: string;
}

export interface ClientePagina {
  content: Cliente[];
  totalElements: number;
  totalPages: number;
  pageNumber: number;
  pageSize: number;
}

export interface TipoDocumentoIdentidad {
  codigo: number;
  descripcion: string;
}
