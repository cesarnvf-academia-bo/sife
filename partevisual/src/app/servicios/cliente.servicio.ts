import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  Cliente,
  ClientePagina,
  ClienteSolicitud,
  TipoDocumentoIdentidad,
} from '../modelos/cliente';

@Injectable({ providedIn: 'root' })
export class ClienteServicio {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/v1/clientes';

  listar(busqueda: string, page: number, size: number): Observable<ClientePagina> {
    let params = new HttpParams().set('page', page).set('size', size);
    const filtro = busqueda.trim();
    if (filtro.length > 0) {
      params = params.set('busqueda', filtro);
    }
    return this.http.get<ClientePagina>(this.url, { params });
  }

  listarTiposDocumentoIdentidad(): Observable<TipoDocumentoIdentidad[]> {
    return this.http.get<TipoDocumentoIdentidad[]>(`${this.url}/tipos-documento-identidad`);
  }

  obtenerPorId(id: number): Observable<Cliente> {
    return this.http.get<Cliente>(`${this.url}/${id}`);
  }

  adicionar(solicitud: ClienteSolicitud): Observable<Cliente> {
    return this.http.post<Cliente>(this.url, solicitud);
  }

  modificar(id: number, solicitud: ClienteSolicitud): Observable<Cliente> {
    return this.http.put<Cliente>(`${this.url}/${id}`, solicitud);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
