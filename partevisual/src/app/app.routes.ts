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
  {
    path: 'sucursal',
    loadComponent: () =>
      import('./paginas/sucursal/sucursal').then((modulo) => modulo.Sucursal),
    data: { miga: 'Sucursal' },
  },
  {
    path: 'puntoventa',
    loadComponent: () =>
      import('./paginas/puntoventa/puntoventa').then((modulo) => modulo.PuntoVenta),
    data: { miga: 'Punto de venta' },
  },
  { path: '**', redirectTo: 'inicio' },
];
