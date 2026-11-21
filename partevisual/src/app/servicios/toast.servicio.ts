import { Injectable, signal } from '@angular/core';

export type TipoToast = 'exito' | 'error' | 'informacion' | 'advertencia';

export interface Toast {
  id: number;
  tipo: TipoToast;
  titulo: string;
  mensaje: string;
}

@Injectable({ providedIn: 'root' })
export class ToastServicio {
  private readonly estado = signal<Toast[]>([]);
  private secuencia = 0;

  readonly toasts = this.estado.asReadonly();

  exito(mensaje: string, titulo = 'Operación correcta'): void {
    this.mostrar('exito', titulo, mensaje);
  }

  error(mensaje: string, titulo = 'Ocurrió un error'): void {
    this.mostrar('error', titulo, mensaje);
  }

  informacion(mensaje: string, titulo = 'Información'): void {
    this.mostrar('informacion', titulo, mensaje);
  }

  advertencia(mensaje: string, titulo = 'Atención'): void {
    this.mostrar('advertencia', titulo, mensaje);
  }

  cerrar(id: number): void {
    this.estado.update((toasts) => toasts.filter((toast) => toast.id !== id));
  }

  private mostrar(tipo: TipoToast, titulo: string, mensaje: string): void {
    const id = ++this.secuencia;
    this.estado.update((toasts) => [...toasts, { id, tipo, titulo, mensaje }]);
    setTimeout(() => this.cerrar(id), 5000);
  }
}
