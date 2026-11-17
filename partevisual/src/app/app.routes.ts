import { Routes } from '@angular/router';

export const rutas: Routes = [
  { path: '', pathMatch: 'full', redirectTo: 'inicio' },
  {
    path: 'inicio',
    loadComponent: () => import('./paginas/inicio').then((modulo) => modulo.Inicio),
    data: { miga: 'Inicio' },
  },
  {
    path: 'empresa',
    loadComponent: () => import('./paginas/empresa/empresa').then((modulo) => modulo.Empresa),
    data: { miga: 'Empresa' },
  },
  { path: '**', redirectTo: 'inicio' },
];
