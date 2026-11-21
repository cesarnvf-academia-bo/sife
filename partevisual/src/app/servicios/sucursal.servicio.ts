import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import { Sucursal, SucursalPagina, SucursalSolicitud } from '../modelos/sucursal';

@Injectable({ providedIn: 'root' })
export class SucursalServicio {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/v1/sucursales';

  listar(busqueda: string, page: number, size: number): Observable<SucursalPagina> {
    let params = new HttpParams().set('page', page).set('size', size);
    const filtro = busqueda.trim();
    if (filtro.length > 0) {
      params = params.set('busqueda', filtro);
    }
    return this.http.get<SucursalPagina>(this.url, { params });
  }

  obtenerPorId(id: number): Observable<Sucursal> {
    return this.http.get<Sucursal>(`${this.url}/${id}`);
  }

  adicionar(solicitud: SucursalSolicitud): Observable<Sucursal> {
    return this.http.post<Sucursal>(this.url, solicitud);
  }

  modificar(id: number, solicitud: SucursalSolicitud): Observable<Sucursal> {
    return this.http.put<Sucursal>(`${this.url}/${id}`, solicitud);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
