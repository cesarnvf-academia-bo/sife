import { Component, inject } from '@angular/core';
import { Icono, type NombreIcono } from '../compartido/icono';
import { Toast, ToastServicio, TipoToast } from '../servicios/toast.servicio';

@Component({
  selector: 'sife-toast-contenedor',
  imports: [Icono],
  template: `
    <div
      class="pointer-events-none fixed top-20 right-4 z-[100] flex w-full max-w-sm flex-col gap-3"
      aria-live="polite"
    >
      @for (toast of toastServicio.toasts(); track toast.id) {
        <div
          class="pointer-events-auto flex items-start gap-3 rounded-xl border bg-white p-4 shadow-lg dark:bg-slate-900"
          [class]="clases(toast.tipo)"
          role="status"
        >
          <sife-icono [nombre]="iconos[toast.tipo]" [tamano]="20" />
          <div class="min-w-0 flex-1">
            <p class="text-sm font-semibold text-slate-900 dark:text-slate-100">
              {{ toast.titulo }}
            </p>
            <p class="mt-0.5 break-words text-sm text-slate-600 dark:text-slate-300">
              {{ toast.mensaje }}
            </p>
          </div>
          <button
            type="button"
            aria-label="Cerrar notificación"
            class="rounded-md p-1 text-slate-400 transition hover:bg-slate-100 hover:text-slate-700 dark:hover:bg-slate-800 dark:hover:text-slate-200"
            (click)="toastServicio.cerrar(toast.id)"
          >
            <sife-icono nombre="cerrar" [tamano]="16" />
          </button>
        </div>
      }
    </div>
  `,
})
export class ToastContenedor {
  protected readonly toastServicio = inject(ToastServicio);

  protected readonly iconos: Record<TipoToast, NombreIcono> = {
    exito: 'exito',
    error: 'error',
    informacion: 'informacion',
    advertencia: 'error',
  };

  protected clases(tipo: TipoToast): string {
    const base =
      'pointer-events-auto flex items-start gap-3 rounded-xl border bg-white p-4 shadow-lg dark:bg-slate-900';
    const estilos: Record<TipoToast, string> = {
      exito:
        'border-emerald-200 text-emerald-600 dark:border-emerald-900 dark:text-emerald-400',
      error: 'border-red-200 text-red-600 dark:border-red-900 dark:text-red-400',
      informacion:
        'border-sky-200 text-sky-600 dark:border-sky-900 dark:text-sky-400',
      advertencia:
        'border-amber-200 text-amber-600 dark:border-amber-900 dark:text-amber-400',
    };
    return `${base} ${estilos[tipo]}`;
  }
}
