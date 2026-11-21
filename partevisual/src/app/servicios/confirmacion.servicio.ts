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
}
