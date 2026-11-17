import { Injectable, signal } from '@angular/core';

type Tema = 'claro' | 'oscuro';

@Injectable({ providedIn: 'root' })
export class EstadoTemaServicio {
  private readonly _temaOscuro = signal(this.obtenerTemaInicial());

  readonly temaOscuro = this._temaOscuro.asReadonly();

  constructor() {
    this.aplicarTema(this._temaOscuro());
  }

  alternarTema(): void {
    const oscuro = !this._temaOscuro();
    this._temaOscuro.set(oscuro);
    this.aplicarTema(oscuro);
    localStorage.setItem('tema', oscuro ? 'oscuro' : 'claro');
  }

  private obtenerTemaInicial(): boolean {
    const guardado = localStorage.getItem('tema') as Tema | null;
    if (guardado) {
      return guardado === 'oscuro';
    }
    return window.matchMedia('(prefers-color-scheme: dark)').matches;
  }

  private aplicarTema(oscuro: boolean): void {
    document.documentElement.classList.toggle('dark', oscuro);
  }
}
