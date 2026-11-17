import { afterNextRender, Component, ElementRef, inject, viewChild } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { BotonIcono } from '../compartido/boton-icono';
import { MigasPan } from '../compartido/migas-pan';
import { EstadoLayoutServicio } from './estado-layout.servicio';

@Component({
  selector: 'sife-area-trabajo',
  imports: [MigasPan, RouterOutlet, BotonIcono],
  host: { class: 'flex min-w-0 flex-1' },
  template: `
    <main
      #areaTrabajo
      class="relative flex-1 overflow-auto bg-slate-50 p-4 md:p-6 dark:bg-slate-950"
    >
      <div class="flex items-center justify-between gap-4">
        <sife-migas-pan />
        @if (estado.pantallaCompleta()) {
          <sife-boton-icono
            icono="minimizar"
            etiqueta="Restaurar área de trabajo"
            (accion)="estado.alternarPantallaCompleta()"
          />
        }
      </div>

      <section class="mt-4">
        <router-outlet />
      </section>
    </main>
  `,
})
export class AreaTrabajo {
  private readonly contenedor = viewChild.required<ElementRef<HTMLElement>>('areaTrabajo');
  protected readonly estado = inject(EstadoLayoutServicio);

  constructor() {
    afterNextRender(() => this.estado.registrarAreaTrabajo(this.contenedor().nativeElement));
  }
}
