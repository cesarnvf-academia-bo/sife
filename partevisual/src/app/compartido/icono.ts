import { Component, computed, input } from '@angular/core';
import {
  LucideBuilding,
  LucideChevronRight,
  LucideDynamicIcon,
  LucideEraser,
  LucideFileText,
  LucideHouse,
  LucideMaximize2,
  LucideMinimize2,
  LucideMoon,
  LucidePanelLeftClose,
  LucidePanelLeftOpen,
  LucidePlus,
  LucideRefreshCw,
  LucideSearch,
  LucideSettings,
  LucideSun,
  LucideUsers,
  type LucideIcon,
} from '@lucide/angular';

const ICONOS = {
  'panel-izquierdo-cerrar': LucidePanelLeftClose,
  'panel-izquierdo-abrir': LucidePanelLeftOpen,
  maximizar: LucideMaximize2,
  minimizar: LucideMinimize2,
  inicio: LucideHouse,
  empresas: LucideBuilding,
  usuarios: LucideUsers,
  documentos: LucideFileText,
  configuracion: LucideSettings,
  'chevron-derecha': LucideChevronRight,
  sol: LucideSun,
  luna: LucideMoon,
  buscar: LucideSearch,
  agregar: LucidePlus,
  actualizar: LucideRefreshCw,
  limpiar: LucideEraser,
} satisfies Record<string, LucideIcon>;

export type NombreIcono = keyof typeof ICONOS;

@Component({
  selector: 'sife-icono',
  imports: [LucideDynamicIcon],
  template: `<svg [lucideIcon]="icono()" [size]="tamano()" [class]="clase()" aria-hidden="true"></svg>`,
})
export class Icono {
  readonly nombre = input.required<NombreIcono>();
  readonly tamano = input(20);
  readonly clase = input('');
  protected readonly icono = computed(() => ICONOS[this.nombre()]);
}
