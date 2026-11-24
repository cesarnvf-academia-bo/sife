import { HttpClient, HttpParams } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  ActividadOpcion,
  Producto,
  ProductoPagina,
  ProductoServicioOpcion,
  ProductoSolicitud,
  UnidadMedidaOpcion,
} from '../modelos/producto';

@Injectable({ providedIn: 'root' })
export class ProductoServicio {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/v1/productos';

  listar(busqueda: string, page: number, size: number): Observable<ProductoPagina> {
    let params = new HttpParams().set('page', page).set('size', size);
    const filtro = busqueda.trim();
    if (filtro.length > 0) {
      params = params.set('busqueda', filtro);
    }
    return this.http.get<ProductoPagina>(this.url, { params });
  }

  listarActividades(): Observable<ActividadOpcion[]> {
    return this.http.get<ActividadOpcion[]>(`${this.url}/actividades`);
  }

  listarProductosServicio(): Observable<ProductoServicioOpcion[]> {
    return this.http.get<ProductoServicioOpcion[]>(`${this.url}/productos-servicio`);
  }

  listarUnidadesMedida(): Observable<UnidadMedidaOpcion[]> {
    return this.http.get<UnidadMedidaOpcion[]>(`${this.url}/unidades-medida`);
  }

  obtenerPorId(id: number): Observable<Producto> {
    return this.http.get<Producto>(`${this.url}/${id}`);
  }

  adicionar(solicitud: ProductoSolicitud): Observable<Producto> {
    return this.http.post<Producto>(this.url, solicitud);
  }

  modificar(id: number, solicitud: ProductoSolicitud): Observable<Producto> {
    return this.http.put<Producto>(`${this.url}/${id}`, solicitud);
  }

  eliminar(id: number): Observable<void> {
    return this.http.delete<void>(`${this.url}/${id}`);
  }
}
