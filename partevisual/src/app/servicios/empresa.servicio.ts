import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Empresa, EmpresaSolicitud } from '../modelos/empresa';

@Injectable({ providedIn: 'root' })
export class EmpresaServicio {
  private readonly http = inject(HttpClient);
  private readonly url = '/api/v1/empresas';

  obtenerPorId(id: number) {
    return this.http.get<Empresa>(`${this.url}/${id}`);
  }

  adicionar(solicitud: EmpresaSolicitud) {
    return this.http.post<Empresa>(this.url, solicitud);
  }

  modificar(id: number, solicitud: EmpresaSolicitud) {
    return this.http.put<Empresa>(`${this.url}/${id}`, solicitud);
  }
}
