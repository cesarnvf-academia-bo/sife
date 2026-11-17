import { Component } from '@angular/core';

@Component({
  selector: 'sife-inicio',
  template: `
    <section
      class="rounded-xl border border-slate-200 bg-white p-6 shadow-sm dark:border-slate-800 dark:bg-slate-900"
    >
      <h1 class="text-2xl font-semibold text-slate-800 dark:text-slate-100">Inicio</h1>
      <p class="mt-2 text-slate-600 dark:text-slate-400">Bienvenido al sistema SIFE.</p>
    </section>
  `,
})
export class Inicio {}
