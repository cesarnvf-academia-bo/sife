import { Component } from '@angular/core';
import { AreaTrabajo } from './layout/area-trabajo';
import { BarraLateral } from './layout/barra-lateral';
import { BarraSuperior } from './layout/barra-superior';

@Component({
  selector: 'sife-raiz',
  imports: [BarraSuperior, BarraLateral, AreaTrabajo],
  templateUrl: './app.html',
  styleUrl: './app.css',
})
export class App {}
