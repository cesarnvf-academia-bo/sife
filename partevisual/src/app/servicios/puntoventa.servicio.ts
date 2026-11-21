import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { CufdRespuesta } from '../modelos/cufd';
import { CuisRespuesta } from '../modelos/cuis';
import {
  PaginaRespuesta,
  PuntoVenta,
  PuntoVentaSolicitud,
} from '../modelos/puntoventa';

@Injectable({ providedIn: 'root' })
export class PuntoVentaServicio {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/v1/puntosventa';

  listar(busqueda: string, page: number, size: number): Observable<PaginaRespuesta<PuntoVenta>> {
    const params = new HttpParams()
      .set('busqueda', busqueda)
      .set('page', page)
      .set('size', size);
    return this.http.get<PaginaRespuesta<PuntoVenta>>(this.url, { params });
  }

  obtenerPorId(id: number): Observable<PuntoVenta> {
    return this.http.get<PuntoVenta>(`${this.url}/${id}`);
  }

  adicionar(solicitud: PuntoVentaSolicitud): Observable<PuntoVenta> {
    return this.http.post<PuntoVenta>(this.url, solicitud);
  }

  modificar(id: number, solicitud: PuntoVentaSolicitud): Observable<PuntoVenta> {
    return this.http.put<PuntoVenta>(`${this.url}/${id}`, solicitud);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }

  obtenerCuis(id: number): Observable<CuisRespuesta> {
    return this.http.post<CuisRespuesta>(`${this.url}/${id}/cuis`, {});
  }

  obtenerCufd(id: number): Observable<CufdRespuesta> {
    return this.http.post<CufdRespuesta>(`${this.url}/${id}/cufd`, {});
  }
}
