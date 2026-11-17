export interface Empresa {
  id: number;
  nit: string;
  razonsocial: string;
  codigoSistema: string | null;
  modalidad: string | null;
  ambiente: string | null;
  rutaCodigo: string | null;
  rutaSincronizacion: string | null;
  rutaOperacion: string | null;
  rutaCompraventa: string | null;
  rutaFirmaDigital: string | null;
  tieneToken: boolean;
  tieneClaveFirmaDigital: boolean;
}

export interface EmpresaSolicitud {
  nit: string;
  razonsocial: string;
  codigoSistema: string | null;
  modalidad: string | null;
  ambiente: string | null;
  rutaCodigo: string | null;
  rutaSincronizacion: string | null;
  rutaOperacion: string | null;
  rutaCompraventa: string | null;
  token: string | null;
  rutaFirmaDigital: string | null;
  claveFirmaDigital: string | null;
}
