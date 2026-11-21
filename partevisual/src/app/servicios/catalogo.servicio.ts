import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';
import {
  CatalogoRespuesta,
  SincronizacionFuncion,
  SincronizacionResumen,
} from '../modelos/catalogo';

@Injectable({ providedIn: 'root' })
export class CatalogoServicio {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/v1/puntosventa';

  listarFunciones(puntoVentaId: number): Observable<SincronizacionFuncion[]> {
    return this.http.get<SincronizacionFuncion[]>(
      `${this.url}/${puntoVentaId}/catalogos/funciones`,
    );
  }

  sincronizar(puntoVentaId: number): Observable<SincronizacionResumen> {
    return this.http.post<SincronizacionResumen>(
      `${this.url}/${puntoVentaId}/catalogos/sincronizar`,
      {},
    );
  }

  obtener(puntoVentaId: number, funcion: string): Observable<CatalogoRespuesta> {
    return this.http.get<CatalogoRespuesta>(
      `${this.url}/${puntoVentaId}/catalogos/${funcion}`,
    );
  }
}
