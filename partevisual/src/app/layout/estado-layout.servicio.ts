import { Injectable, signal } from '@angular/core';

@Injectable({ providedIn: 'root' })
export class EstadoLayoutServicio {
  private readonly _barraLateralVisible = signal(
    typeof window === 'undefined' || window.matchMedia('(min-width: 768px)').matches,
  );
  private readonly _pantallaCompleta = signal(false);
  private elementoAreaTrabajo: HTMLElement | null = null;

  readonly barraLateralVisible = this._barraLateralVisible.asReadonly();
  readonly pantallaCompleta = this._pantallaCompleta.asReadonly();

  constructor() {
    document.addEventListener('fullscreenchange', () => {
      this._pantallaCompleta.set(Boolean(document.fullscreenElement));
    });
  }

  alternarBarraLateral(): void {
    this._barraLateralVisible.update((visible) => !visible);
  }

  ocultarBarraLateral(): void {
    this._barraLateralVisible.set(false);
  }

  registrarAreaTrabajo(elemento: HTMLElement | null): void {
    this.elementoAreaTrabajo = elemento;
  }

  async alternarPantallaCompleta(): Promise<void> {
    if (document.fullscreenElement) {
      await document.exitFullscreen();
      return;
    }

    const elemento = this.elementoAreaTrabajo;
    if (!elemento) {
      return;
    }

    try {
      await elemento.requestFullscreen();
    } catch {
      this._pantallaCompleta.set(false);
    }
  }
}
