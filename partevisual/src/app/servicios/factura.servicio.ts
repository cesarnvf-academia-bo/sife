import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  CatalogoFactura,
  Factura,
  FacturaAnulacionSolicitud,
  FacturaPagina,
  FacturaSolicitud,
} from '../modelos/factura';

@Injectable({ providedIn: 'root' })
export class FacturaServicio {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/v1/facturas';

  listar(busqueda: string, page: number, size: number): Observable<FacturaPagina> {
    let params = new HttpParams().set('page', page).set('size', size);
    const filtro = busqueda.trim();
    if (filtro.length > 0) {
      params = params.set('busqueda', filtro);
    }
    return this.http.get<FacturaPagina>(this.url, { params });
  }

  catalogos(): Observable<CatalogoFactura> {
    return this.http.get<CatalogoFactura>(`${this.url}/catalogos`);
  }

  obtenerPorId(id: number): Observable<Factura> {
    return this.http.get<Factura>(`${this.url}/${id}`);
  }

  emitir(solicitud: FacturaSolicitud): Observable<Factura> {
    return this.http.post<Factura>(this.url, solicitud);
  }

  anular(id: number, codigoMotivo: number): Observable<Factura> {
    const solicitud: FacturaAnulacionSolicitud = { codigoMotivo };
    return this.http.post<Factura>(`${this.url}/${id}/anular`, solicitud);
  }

  revertir(id: number): Observable<Factura> {
    return this.http.post<Factura>(`${this.url}/${id}/revertir`, {});
  }

  verXml(id: number): Observable<Blob> {
    return this.http.get(`${this.url}/${id}/xml`, { responseType: 'blob' });
  }
}
