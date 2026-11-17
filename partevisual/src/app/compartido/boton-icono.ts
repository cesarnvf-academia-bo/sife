import { Component, computed, input, output } from '@angular/core';
import { Icono, type NombreIcono } from './icono';

type VarianteBoton = 'primario' | 'secundario' | 'fantasma';

@Component({
  selector: 'sife-boton-icono',
  imports: [Icono],
  template: `
    <button
      type="button"
      [class]="clases()"
      [attr.aria-label]="etiqueta()"
      [attr.title]="titulo() || etiqueta()"
      (click)="accion.emit()"
    >
      <sife-icono [nombre]="icono()" [tamano]="tamanoIcono()" />
      @if (texto()) {
        <span>{{ texto() }}</span>
      }
    </button>
  `,
})
export class BotonIcono {
  readonly icono = input.required<NombreIcono>();
  readonly etiqueta = input.required<string>();
  readonly titulo = input('');
  readonly texto = input('');
  readonly variante = input<VarianteBoton>('fantasma');
  readonly tamanoIcono = input(20);
  readonly accion = output<void>();

  protected readonly clases = computed(() => {
    const base =
      'inline-flex items-center justify-center gap-2 rounded-lg px-3 py-2 text-sm font-medium transition-colors focus:outline-none focus-visible:ring-2 focus-visible:ring-sky-500 disabled:cursor-not-allowed disabled:opacity-50';
    const variantes: Record<VarianteBoton, string> = {
      primario:
        'bg-sky-600 text-white hover:bg-sky-700 dark:bg-sky-500 dark:hover:bg-sky-400',
      secundario:
        'bg-slate-100 text-slate-700 hover:bg-slate-200 dark:bg-slate-800 dark:text-slate-200 dark:hover:bg-slate-700',
      fantasma:
        'text-slate-600 hover:bg-slate-100 hover:text-slate-900 dark:text-slate-300 dark:hover:bg-slate-800 dark:hover:text-white',
    };
    const contenido = this.texto() ? '' : 'aspect-square px-2';
    return `${base} ${variantes[this.variante()]} ${contenido}`;
  });
}
