import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { BotonIcono } from '../../compartido/boton-icono';
import { Modal } from '../../compartido/modal';
import { Sucursal as SucursalModelo, SucursalSolicitud } from '../../modelos/sucursal';
import { ConfirmacionServicio } from '../../servicios/confirmacion.servicio';
import { SucursalServicio } from '../../servicios/sucursal.servicio';
import { ToastServicio } from '../../servicios/toast.servicio';

@Component({
  selector: 'sife-sucursal',
  imports: [ReactiveFormsModule, BotonIcono, Modal],
  templateUrl: './sucursal.html',
})
export class Sucursal implements OnInit {
  private readonly constructorFormulario = inject(FormBuilder);
  private readonly sucursalServicio = inject(SucursalServicio);
  private readonly toastServicio = inject(ToastServicio);
  private readonly confirmacionServicio = inject(ConfirmacionServicio);

  protected readonly sucursales = signal<SucursalModelo[]>([]);
  protected readonly busqueda = signal('');
  protected readonly tamano = signal(5);
  protected readonly pagina = signal(0);
  protected readonly total = signal(0);
  protected readonly totalPaginas = signal(0);
  protected readonly cargando = signal(false);
  protected readonly guardando = signal(false);
  protected readonly modalAbierto = signal(false);
  protected readonly idEnEdicion = signal<number | null>(null);

  protected readonly tituloModal = computed(() =>
    this.idEnEdicion() === null ? 'Adicionar sucursal' : 'Editar sucursal',
  );
  protected readonly desde = computed(() =>
    this.total() === 0 ? 0 : this.pagina() * this.tamano() + 1,
  );
  protected readonly esPrimera = computed(() => this.pagina() === 0);
  protected readonly esUltima = computed(
    () => this.totalPaginas() === 0 || this.pagina() >= this.totalPaginas() - 1,
  );

  protected readonly formulario = this.constructorFormulario.nonNullable.group({
    codigo: ['', [Validators.required, Validators.maxLength(20)]],
    sucursal: ['', [Validators.required, Validators.maxLength(250)]],
    estado: [true],
  });

  ngOnInit(): void {
    this.listar();
  }

  protected listar(): void {
    this.cargando.set(true);

    this.sucursalServicio
      .listar(this.busqueda(), this.pagina(), this.tamano())
      .subscribe({
        next: (respuesta) => {
          this.sucursales.set(respuesta.content);
          this.total.set(respuesta.totalElements);
          this.totalPaginas.set(respuesta.totalPages);
          this.pagina.set(respuesta.pageNumber);
          this.tamano.set(respuesta.pageSize);
          this.cargando.set(false);
        },
        error: (error: HttpErrorResponse) => {
          this.toastServicio.error(this.obtenerMensajeError(error));
          this.cargando.set(false);
        },
      });
  }

  protected buscar(): void {
    this.pagina.set(0);
    this.listar();
  }

  protected limpiar(): void {
    this.busqueda.set('');
    this.pagina.set(0);
    this.listar();
  }

  protected cambiarTamano(valor: string): void {
    const tamano = Number(valor);
    this.tamano.set([5, 10, 15, 20].includes(tamano) ? tamano : 5);
    this.pagina.set(0);
    this.listar();
  }

  protected primero(): void {
    this.pagina.set(0);
    this.listar();
  }

  protected anterior(): void {
    if (!this.esPrimera()) {
      this.pagina.update((valor) => valor - 1);
      this.listar();
    }
  }

  protected siguiente(): void {
    if (!this.esUltima()) {
      this.pagina.update((valor) => valor + 1);
      this.listar();
    }
  }

  protected ultimo(): void {
    if (!this.esUltima()) {
      this.pagina.set(Math.max(this.totalPaginas() - 1, 0));
      this.listar();
    }
  }

  protected adicionar(): void {
    this.idEnEdicion.set(null);
    this.formulario.reset({ codigo: '', sucursal: '', estado: true });
    this.modalAbierto.set(true);
  }

  protected editar(sucursal: SucursalModelo): void {
    this.idEnEdicion.set(sucursal.id);
    this.formulario.patchValue({
      codigo: sucursal.codigo,
      sucursal: sucursal.sucursal,
      estado: sucursal.estado,
    });
    this.modalAbierto.set(true);
  }

  protected cancelar(): void {
    this.modalAbierto.set(false);
    this.idEnEdicion.set(null);
    this.formulario.reset({ codigo: '', sucursal: '', estado: true });
  }

  protected guardar(): void {
    if (this.guardando()) {
      return;
    }

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      this.toastServicio.advertencia('Complete los campos obligatorios.');
      return;
    }

    const valor = this.formulario.getRawValue();
    const solicitud: SucursalSolicitud = {
      codigo: valor.codigo.trim(),
      sucursal: valor.sucursal.trim(),
      estado: valor.estado,
    };
    const id = this.idEnEdicion();
    const eraNuevo = id === null;

    this.guardando.set(true);

    const peticion = eraNuevo
      ? this.sucursalServicio.adicionar(solicitud)
      : this.sucursalServicio.modificar(id as number, solicitud);

    peticion.subscribe({
      next: () => {
        this.guardando.set(false);
        this.cancelar();
        this.toastServicio.exito(
          eraNuevo
            ? 'Sucursal adicionada correctamente.'
            : 'Sucursal modificada correctamente.',
        );
        this.listar();
      },
      error: (error: HttpErrorResponse) => {
        this.guardando.set(false);
        this.toastServicio.error(this.obtenerMensajeError(error));
      },
    });
  }

  protected async borrar(sucursal: SucursalModelo): Promise<void> {
    const confirmado = await this.confirmacionServicio.confirmarBorrado(
      sucursal.sucursal,
      'sucursal',
    );
    if (!confirmado) {
      return;
    }

    this.cargando.set(true);

    this.sucursalServicio.eliminar(sucursal.id).subscribe({
      next: () => {
        this.cargando.set(false);
        this.toastServicio.exito('Sucursal borrada correctamente.');
        this.listar();
      },
      error: (error: HttpErrorResponse) => {
        this.cargando.set(false);
        this.toastServicio.error(this.obtenerMensajeError(error));
      },
    });
  }

  private obtenerMensajeError(error: HttpErrorResponse): string {
    if (error.status === 0) {
      return 'No se pudo conectar con la parte lógica.';
    }

    const detalle = error.error?.detail;
    return typeof detalle === 'string' ? detalle : 'Ocurrió un error inesperado.';
  }
}
