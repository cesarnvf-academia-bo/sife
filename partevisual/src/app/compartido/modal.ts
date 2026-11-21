import { Component, HostListener, computed, input, output } from '@angular/core';
import { BotonIcono } from './boton-icono';

let secuenciaModal = 0;

@Component({
  selector: 'sife-modal',
  imports: [BotonIcono],
  template: `
    @if (abierto()) {
      <div class="fixed inset-0 z-50 flex items-center justify-center p-4">
        <div
          class="absolute inset-0 bg-slate-950/60 backdrop-blur-sm"
          aria-hidden="true"
          (click)="cerrar.emit()"
        ></div>
        <div
          class="relative z-10 max-h-[90vh] w-full overflow-hidden rounded-xl border border-slate-200 bg-white shadow-2xl dark:border-slate-800 dark:bg-slate-900"
          [class]="anchoClase()"
          role="dialog"
          aria-modal="true"
          [attr.aria-labelledby]="idTitulo"
        >
          <header
            class="flex items-start justify-between gap-4 border-b border-slate-200 px-6 py-4 dark:border-slate-800"
          >
            <div>
              <h2
                [id]="idTitulo"
                class="text-lg font-semibold text-slate-900 dark:text-slate-100"
              >
                {{ titulo() }}
              </h2>
              @if (subtitulo()) {
                <p class="mt-1 text-sm text-slate-600 dark:text-slate-400">
                  {{ subtitulo() }}
                </p>
              }
            </div>
            <sife-boton-icono
              icono="cerrar"
              etiqueta="Cerrar ventana"
              texto=""
              variante="fantasma"
              (accion)="cerrar.emit()"
            />
          </header>
          <div class="max-h-[70vh] overflow-y-auto px-6 py-5">
            <ng-content />
          </div>
        </div>
      </div>
    }
  `,
})
export class Modal {
  readonly abierto = input(false);
  readonly titulo = input.required<string>();
  readonly subtitulo = input('');
  readonly ancho = input<'sm' | 'md' | 'lg' | 'xl'>('lg');
  readonly cerrar = output<void>();

  protected readonly idTitulo = `sife-modal-titulo-${++secuenciaModal}`;

  protected readonly anchoClase = computed(() => {
    const anchos: Record<'sm' | 'md' | 'lg' | 'xl', string> = {
      sm: 'max-w-sm',
      md: 'max-w-md',
      lg: 'max-w-3xl',
      xl: 'max-w-5xl',
    };
    return anchos[this.ancho()];
  });

  @HostListener('document:keydown.escape')
  protected alEscape(): void {
    if (this.abierto()) {
      this.cerrar.emit();
    }
  }
}
