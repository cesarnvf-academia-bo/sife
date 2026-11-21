import { Component, computed, input } from '@angular/core';
import {
  LucideBuilding,
  LucideChevronLeft,
  LucideChevronRight,
  LucideChevronsLeft,
  LucideChevronsRight,
  LucideCircleAlert,
  LucideCircleCheck,
  LucideDynamicIcon,
  LucideEraser,
  LucideFileText,
  LucideHouse,
  LucideInfo,
  LucideKeyRound,
  LucideMaximize2,
  LucideMinimize2,
  LucideMoon,
  LucidePanelLeftClose,
  LucidePanelLeftOpen,
  LucidePencil,
  LucidePlus,
  LucideQrCode,
  LucideRefreshCw,
  LucideScanLine,
  LucideSearch,
  LucideSettings,
  LucideStore,
  LucideSun,
  LucideTrash2,
  LucideUsers,
  LucideX,
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
  sucursales: LucideStore,
  editar: LucidePencil,
  borrar: LucideTrash2,
  primero: LucideChevronsLeft,
  anterior: LucideChevronLeft,
  siguiente: LucideChevronRight,
  ultimo: LucideChevronsRight,
  cerrar: LucideX,
  exito: LucideCircleCheck,
  error: LucideCircleAlert,
  informacion: LucideInfo,
  puntoventa: LucideScanLine,
  cuis: LucideKeyRound,
  cufd: LucideQrCode,
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
