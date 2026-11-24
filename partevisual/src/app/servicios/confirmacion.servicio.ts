import { Injectable } from '@angular/core';
import Swal from 'sweetalert2';

@Injectable({ providedIn: 'root' })
export class ConfirmacionServicio {
  async confirmarBorrado(nombre: string, entidad = 'registro'): Promise<boolean> {
    const resultado = await Swal.fire({
      title: `¿Borrar ${entidad}?`,
      text: `Se borrará ${nombre}. Esta acción no se puede deshacer.`,
      icon: 'warning',
      showCancelButton: true,
      confirmButtonColor: '#dc2626',
      cancelButtonColor: '#64748b',
      confirmButtonText: 'Sí, borrar',
      cancelButtonText: 'Cancelar',
      reverseButtons: true,
      focusCancel: true,
    });

    return resultado.isConfirmed;
  }

  async confirmar(
    titulo: string,
    mensaje: string,
    confirmarTexto: string,
    entidad: string,
  ): Promise<boolean> {
    const resultado = await Swal.fire({
      title: titulo,
      text: mensaje,
      icon: 'question',
      showCancelButton: true,
      confirmButtonColor: '#0284c7',
      cancelButtonColor: '#64748b',
      confirmButtonText: confirmarTexto,
      cancelButtonText: 'Cancelar',
      confirmButtonAriaLabel: `${confirmarTexto} ${entidad}`,
      reverseButtons: true,
      focusCancel: true,
    });

    return resultado.isConfirmed;
  }
}
