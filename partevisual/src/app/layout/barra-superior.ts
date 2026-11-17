import { Component, inject } from '@angular/core';
import { BotonIcono } from '../compartido/boton-icono';
import { EstadoLayoutServicio } from './estado-layout.servicio';
import { EstadoTemaServicio } from './estado-tema.servicio';

@Component({
  selector: 'sife-barra-superior',
  imports: [BotonIcono],
  template: `
    <header
      class="flex h-16 shrink-0 items-center justify-between border-b border-slate-200 bg-white px-4 shadow-sm dark:border-slate-800 dark:bg-slate-900"
    >
      <div class="flex items-center gap-3">
        <sife-boton-icono
          [icono]="
            estado.barraLateralVisible() ? 'panel-izquierdo-cerrar' : 'panel-izquierdo-abrir'
          "
          [etiqueta]="
            estado.barraLateralVisible() ? 'Ocultar barra lateral' : 'Mostrar barra lateral'
          "
          (accion)="estado.alternarBarraLateral()"
        />
        <div class="flex items-center gap-2">
          <span
            class="flex h-8 w-8 items-center justify-center rounded-lg bg-sky-600 text-sm font-bold text-white dark:bg-sky-500"
            >S</span
          >
          <span class="text-lg font-semibold tracking-tight text-slate-800 dark:text-slate-100"
            >SIFE</span
          >
        </div>
      </div>

      <div class="flex items-center gap-2">
        <sife-boton-icono
          [icono]="tema.temaOscuro() ? 'sol' : 'luna'"
          [etiqueta]="tema.temaOscuro() ? 'Activar tema claro' : 'Activar tema oscuro'"
          (accion)="tema.alternarTema()"
        />
        <sife-boton-icono
          [icono]="estado.pantallaCompleta() ? 'minimizar' : 'maximizar'"
          [etiqueta]="
            estado.pantallaCompleta() ? 'Restaurar área de trabajo' : 'Maximizar área de trabajo'
          "
          (accion)="estado.alternarPantallaCompleta()"
        />
      </div>
    </header>
  `,
})
export class BarraSuperior {
  protected readonly estado = inject(EstadoLayoutServicio);
  protected readonly tema = inject(EstadoTemaServicio);
}
