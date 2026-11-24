import { Component, computed, inject } from '@angular/core';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { Icono, type NombreIcono } from '../compartido/icono';
import { EstadoLayoutServicio } from './estado-layout.servicio';

interface OpcionMenu {
  etiqueta: string;
  ruta: string;
  icono: NombreIcono;
}

const OPCIONES_MENU: OpcionMenu[] = [
  { etiqueta: 'Inicio', ruta: '/inicio', icono: 'inicio' },
  { etiqueta: 'Empresa', ruta: '/empresa', icono: 'empresas' },
  { etiqueta: 'Sucursal', ruta: '/sucursal', icono: 'sucursales' },
  { etiqueta: 'Cliente', ruta: '/cliente', icono: 'usuarios' },
  { etiqueta: 'Producto', ruta: '/producto', icono: 'documentos' },
  { etiqueta: 'Factura', ruta: '/factura', icono: 'documentos' },
  { etiqueta: 'Punto de venta', ruta: '/puntoventa', icono: 'puntoventa' },
];

@Component({
  selector: 'sife-barra-lateral',
  imports: [RouterLink, RouterLinkActive, Icono],
  template: `
    @if (estado.barraLateralVisible()) {
      <div
        class="fixed inset-0 z-30 bg-slate-900/40 md:hidden dark:bg-black/60"
        aria-hidden="true"
        (click)="estado.ocultarBarraLateral()"
      ></div>
    }

    <aside [class]="clases()" aria-label="Menú principal">
      <div class="flex h-16 items-center border-b border-slate-200 px-4 dark:border-slate-800">
        <span
          class="text-xs font-semibold uppercase tracking-widest text-slate-400 dark:text-slate-500"
          >Menú</span
        >
      </div>

      <nav class="flex flex-col gap-1 p-3">
        @for (opcion of opciones; track opcion.ruta) {
          <a
            [routerLink]="opcion.ruta"
            routerLinkActive="bg-sky-50 text-sky-700 dark:bg-sky-950 dark:text-sky-300"
            [routerLinkActiveOptions]="{ exact: true }"
            class="flex items-center gap-3 rounded-lg px-3 py-2 text-sm font-medium text-slate-600 transition-colors hover:bg-slate-100 hover:text-slate-900 dark:text-slate-300 dark:hover:bg-slate-800 dark:hover:text-white"
            (click)="seleccionarOpcion()"
          >
            <sife-icono [nombre]="opcion.icono" [tamano]="18" />
            <span>{{ opcion.etiqueta }}</span>
          </a>
        }
      </nav>
    </aside>
  `,
})
export class BarraLateral {
  protected readonly opciones = OPCIONES_MENU;
  protected readonly estado = inject(EstadoLayoutServicio);

  protected readonly clases = computed(() => {
    const visible = this.estado.barraLateralVisible();
    return [
      'fixed inset-y-0 left-0 z-40 h-full w-64 shrink-0 border-r border-slate-200 bg-white transition-transform duration-200 md:static md:translate-x-0 dark:border-slate-800 dark:bg-slate-900',
      visible ? 'translate-x-0' : '-translate-x-full',
      visible ? 'md:block' : 'md:hidden',
    ].join(' ');
  });

  protected seleccionarOpcion(): void {
    if (window.matchMedia('(max-width: 767px)').matches) {
      this.estado.ocultarBarraLateral();
    }
  }
}
