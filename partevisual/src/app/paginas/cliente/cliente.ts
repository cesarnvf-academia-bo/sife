import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { BotonIcono } from '../../compartido/boton-icono';
import { Modal } from '../../compartido/modal';
import {
  Cliente as ClienteModelo,
  ClienteSolicitud,
  TipoDocumentoIdentidad,
} from '../../modelos/cliente';
import { ClienteServicio } from '../../servicios/cliente.servicio';
import { ConfirmacionServicio } from '../../servicios/confirmacion.servicio';
import { ToastServicio } from '../../servicios/toast.servicio';

@Component({
  selector: 'sife-cliente',
  imports: [ReactiveFormsModule, BotonIcono, Modal],
  templateUrl: './cliente.html',
})
export class Cliente implements OnInit {
  private readonly constructorFormulario = inject(FormBuilder);
  private readonly clienteServicio = inject(ClienteServicio);
  private readonly toastServicio = inject(ToastServicio);
  private readonly confirmacionServicio = inject(ConfirmacionServicio);

  protected readonly clientes = signal<ClienteModelo[]>([]);
  protected readonly tiposDocumento = signal<TipoDocumentoIdentidad[]>([]);
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
    this.idEnEdicion() === null ? 'Adicionar cliente' : 'Editar cliente',
  );
  protected readonly desde = computed(() =>
    this.total() === 0 ? 0 : this.pagina() * this.tamano() + 1,
  );
  protected readonly esPrimera = computed(() => this.pagina() === 0);
  protected readonly esUltima = computed(
    () => this.totalPaginas() === 0 || this.pagina() >= this.totalPaginas() - 1,
  );

  protected readonly formulario = this.constructorFormulario.nonNullable.group({
    codigoInterno: ['', [Validators.required, Validators.maxLength(50)]],
    cliente: ['', [Validators.required, Validators.maxLength(250)]],
    dit: ['', [Validators.required, Validators.maxLength(50)]],
    tipo: [0, [Validators.required, Validators.min(1)]],
    correo: ['', [Validators.required, Validators.email, Validators.maxLength(250)]],
  });

  ngOnInit(): void {
    this.cargarTipos();
    this.listar();
  }

  protected listar(): void {
    this.cargando.set(true);

    this.clienteServicio
      .listar(this.busqueda(), this.pagina(), this.tamano())
      .subscribe({
        next: (respuesta) => {
          this.clientes.set(respuesta.content);
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
    this.formulario.reset({
      codigoInterno: '',
      cliente: '',
      dit: '',
      tipo: this.tiposDocumento()[0]?.codigo ?? 0,
      correo: '',
    });
    this.modalAbierto.set(true);
  }

  protected editar(cliente: ClienteModelo): void {
    this.idEnEdicion.set(cliente.id);
    this.formulario.patchValue({
      codigoInterno: cliente.codigoInterno,
      cliente: cliente.cliente,
      dit: cliente.dit,
      tipo: cliente.tipo,
      correo: cliente.correo,
    });
    this.modalAbierto.set(true);
  }

  protected cancelar(): void {
    this.modalAbierto.set(false);
    this.idEnEdicion.set(null);
    this.formulario.reset({
      codigoInterno: '',
      cliente: '',
      dit: '',
      tipo: this.tiposDocumento()[0]?.codigo ?? 0,
      correo: '',
    });
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
    const solicitud: ClienteSolicitud = {
      codigoInterno: valor.codigoInterno.trim(),
      cliente: valor.cliente.trim(),
      dit: valor.dit.trim(),
      tipo: Number(valor.tipo),
      correo: valor.correo.trim(),
    };
    const id = this.idEnEdicion();
    const eraNuevo = id === null;

    this.guardando.set(true);

    const peticion = eraNuevo
      ? this.clienteServicio.adicionar(solicitud)
      : this.clienteServicio.modificar(id as number, solicitud);

    peticion.subscribe({
      next: () => {
        this.guardando.set(false);
        this.cancelar();
        this.toastServicio.exito(
          eraNuevo
            ? 'Cliente adicionado correctamente.'
            : 'Cliente modificado correctamente.',
        );
        this.listar();
      },
      error: (error: HttpErrorResponse) => {
        this.guardando.set(false);
        this.toastServicio.error(this.obtenerMensajeError(error));
      },
    });
  }

  protected async borrar(cliente: ClienteModelo): Promise<void> {
    const confirmado = await this.confirmacionServicio.confirmarBorrado(
      cliente.cliente,
      'cliente',
    );
    if (!confirmado) {
      return;
    }

    this.cargando.set(true);

    this.clienteServicio.eliminar(cliente.id).subscribe({
      next: () => {
        this.cargando.set(false);
        this.toastServicio.exito('Cliente borrado correctamente.');
        this.listar();
      },
      error: (error: HttpErrorResponse) => {
        this.cargando.set(false);
        this.toastServicio.error(this.obtenerMensajeError(error));
      },
    });
  }

  private cargarTipos(): void {
    this.clienteServicio.listarTiposDocumentoIdentidad().subscribe({
      next: (tipos) => {
        this.tiposDocumento.set(tipos);
        const seleccionado = this.formulario.controls.tipo.value;
        if (tipos.length > 0 && !tipos.some((tipo) => tipo.codigo === seleccionado)) {
          this.formulario.controls.tipo.setValue(tipos[0].codigo);
        }
      },
      error: (error: HttpErrorResponse) => {
        this.toastServicio.error(this.obtenerMensajeError(error));
      },
    });
  }

  private obtenerMensajeError(error: HttpErrorResponse): string {
    if (error.status === 0) {
      return 'No se pudo conectar con la parte lógica.';
    }

    if (typeof error.error === 'string') {
      return error.error;
    }

    const errores = error.error?.errores as Record<string, string> | undefined;
    if (errores) {
      const primero = Object.values(errores)[0];
      if (typeof primero === 'string') {
        return primero;
      }
    }

    const detalle = error.error?.detail;
    return typeof detalle === 'string' ? detalle : 'Ocurrió un error inesperado.';
  }
}
