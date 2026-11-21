export type TipoCatalogo =
  | 'PARAMETRICA'
  | 'ACTIVIDAD'
  | 'FECHA_HORA'
  | 'LEYENDA'
  | 'ACTIVIDAD_DOCUMENTO_SECTOR'
  | 'PRODUCTO_SERVICIO';

export interface SincronizacionFuncion {
  nombre: string;
  etiqueta: string;
  tipo: TipoCatalogo;
}

export interface MensajeSincronizacion {
  codigo: number | null;
  descripcion: string | null;
}

export interface SincronizacionFuncionResultado {
  funcion: string;
  etiqueta: string;
  estado: 'EXITOSA' | 'FALLIDA';
  registros: number;
  error: string | null;
  mensajes: MensajeSincronizacion[];
}

export interface SincronizacionResumen {
  puntoVentaId: number;
  cuis: string;
  total: number;
  exitosas: number;
  fallidas: number;
  resultados: SincronizacionFuncionResultado[];
}

export interface CatalogoFila {
  codigo: string | null;
  descripcion: string | null;
  detalle: string | null;
  nandina: string | null;
}

export interface CatalogoRespuesta {
  funcion: string;
  etiqueta: string;
  ejecucionId: number | null;
  ejecutadoEn: string | null;
  columnas: string[];
  filas: CatalogoFila[];
}
