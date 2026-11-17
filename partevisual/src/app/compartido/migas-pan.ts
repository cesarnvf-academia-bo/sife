import { Component, inject, signal } from '@angular/core';
import { takeUntilDestroyed } from '@angular/core/rxjs-interop';
import { ActivatedRoute, NavigationEnd, Router, RouterLink } from '@angular/router';
import { filter } from 'rxjs';
import { Icono } from './icono';

interface Miga {
  etiqueta: string;
  url: string;
}

@Component({
  selector: 'sife-migas-pan',
  imports: [RouterLink, Icono],
  template: `
    <nav
      aria-label="Migas de pan"
      class="flex items-center text-sm text-slate-500 dark:text-slate-400"
    >
      <ol class="flex flex-wrap items-center gap-1">
        @for (miga of migas(); track miga.url; let ultima = $last) {
          <li class="flex items-center gap-1">
            @if (!$first) {
              <sife-icono
                nombre="chevron-derecha"
                [tamano]="16"
                clase="text-slate-400 dark:text-slate-600"
              />
            }
            @if (ultima) {
              <span
                class="flex items-center gap-1 font-medium text-slate-700 dark:text-slate-200"
                aria-current="page"
              >
                @if ($first) {
                  <sife-icono nombre="inicio" [tamano]="16" />
                }
                {{ miga.etiqueta }}
              </span>
            } @else {
              <a
                [routerLink]="miga.url"
                class="flex items-center gap-1 rounded px-1 py-0.5 hover:text-sky-700 dark:hover:text-sky-400"
              >
                @if ($first) {
                  <sife-icono nombre="inicio" [tamano]="16" />
                }
                {{ miga.etiqueta }}
              </a>
            }
          </li>
        }
      </ol>
    </nav>
  `,
})
export class MigasPan {
  private readonly enrutador = inject(Router);
  private readonly ruta = inject(ActivatedRoute);
  protected readonly migas = signal<Miga[]>([]);

  constructor() {
    this.enrutador.events
      .pipe(
        filter((evento): evento is NavigationEnd => evento instanceof NavigationEnd),
        takeUntilDestroyed(),
      )
      .subscribe(() => this.actualizarMigas());

    this.actualizarMigas();
  }

  private actualizarMigas(): void {
    const migas: Miga[] = [];
    let rutaActual = this.ruta.root;
    let url = '';

    while (rutaActual.firstChild) {
      rutaActual = rutaActual.firstChild;
      const segmentos = rutaActual.snapshot.url.map((segmento) => segmento.path).filter(Boolean);

      if (segmentos.length > 0) {
        url += `/${segmentos.join('/')}`;
      }

      const etiqueta = rutaActual.snapshot.data['miga'] as string | undefined;
      if (etiqueta) {
        migas.push({ etiqueta, url: url || '/' });
      }
    }

    const inicio: Miga = { etiqueta: 'Inicio', url: '/inicio' };
    const sinInicio = migas.filter((miga) => miga.url !== inicio.url);
    this.migas.set([inicio, ...sinInicio]);
  }
}
