import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { BotonIcono } from '../../compartido/boton-icono';
import { Modal } from '../../compartido/modal';
import {
  CatalogoRespuesta,
  SincronizacionFuncion,
} from '../../modelos/catalogo';
import {
  PuntoVenta as PuntoVentaModelo,
  PuntoVentaSolicitud,
  TIPOS_PUNTO_VENTA,
} from '../../modelos/puntoventa';
import { Sucursal } from '../../modelos/sucursal';
import { CatalogoServicio } from '../../servicios/catalogo.servicio';
import { ConfirmacionServicio } from '../../servicios/confirmacion.servicio';
import { PuntoVentaServicio } from '../../servicios/puntoventa.servicio';
import { SucursalServicio } from '../../servicios/sucursal.servicio';
import { ToastServicio } from '../../servicios/toast.servicio';

interface CodigosPuntoVenta {
  cuis?: string;
  cufd?: string;
}

@Component({
  selector: 'sife-puntoventa',
  imports: [ReactiveFormsModule, BotonIcono, Modal],
  templateUrl: './puntoventa.html',
})
export class PuntoVenta implements OnInit {
  private readonly constructorFormulario = inject(FormBuilder);
  private readonly puntoVentaServicio = inject(PuntoVentaServicio);
  private readonly sucursalServicio = inject(SucursalServicio);
  private readonly toastServicio = inject(ToastServicio);
  private readonly confirmacionServicio = inject(ConfirmacionServicio);
  private readonly catalogoServicio = inject(CatalogoServicio);

  protected readonly puntosVenta = signal<PuntoVentaModelo[]>([]);
  protected readonly sucursales = signal<Sucursal[]>([]);
  protected readonly busqueda = signal('');
  protected readonly tamano = signal(5);
  protected readonly pagina = signal(0);
  protected readonly total = signal(0);
  protected readonly totalPaginas = signal(0);
  protected readonly cargando = signal(false);
  protected readonly guardando = signal(false);
  protected readonly modalAbierto = signal(false);
  protected readonly idEnEdicion = signal<number | null>(null);
  protected readonly obteniendoCuis = signal<number | null>(null);
  protected readonly obteniendoCufd = signal<number | null>(null);
  protected readonly codigosPorPuntoVenta = signal<Record<number, CodigosPuntoVenta>>({});
  protected readonly sincronizando = signal<number | null>(null);
  protected readonly catalogoModalAbierto = signal(false);
  protected readonly catalogoPuntoVenta = signal<PuntoVentaModelo | null>(null);
  protected readonly catalogoFunciones = signal<SincronizacionFuncion[]>([]);
  protected readonly catalogoFuncionSeleccionada = signal('');
  protected readonly catalogo = signal<CatalogoRespuesta | null>(null);
  protected readonly cargandoCatalogo = signal(false);
  protected readonly tipos = TIPOS_PUNTO_VENTA;

  protected readonly tituloModal = computed(() =>
    this.idEnEdicion() === null ? 'Adicionar punto de venta' : 'Editar punto de venta',
  );
  protected readonly desde = computed(() =>
    this.total() === 0 ? 0 : this.pagina() * this.tamano() + 1,
  );
  protected readonly esPrimera = computed(() => this.pagina() === 0);
  protected readonly esUltima = computed(
    () => this.totalPaginas() === 0 || this.pagina() >= this.totalPaginas() - 1,
  );

  protected readonly formulario = this.constructorFormulario.nonNullable.group({
    sucursalId: [0, [Validators.required, Validators.min(1)]],
    codigo: [
      '',
      [
        Validators.required,
        Validators.pattern(/^\d+$/),
        Validators.maxLength(20),
      ],
    ],
    puntoventa: ['', [Validators.required, Validators.maxLength(250)]],
    tipo: [5, [Validators.required, Validators.min(1), Validators.max(6)]],
    estado: [true],
  });

  ngOnInit(): void {
    this.cargarSucursales();
    this.listar();
  }

  protected listar(): void {
    this.cargando.set(true);

    this.puntoVentaServicio
      .listar(this.busqueda(), this.pagina(), this.tamano())
      .subscribe({
        next: (respuesta) => {
          this.puntosVenta.set(respuesta.content);
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
      sucursalId: this.sucursales()[0]?.id ?? 0,
      codigo: '',
      puntoventa: '',
      tipo: 5,
      estado: true,
    });
    this.modalAbierto.set(true);
  }

  protected editar(puntoVenta: PuntoVentaModelo): void {
    this.idEnEdicion.set(puntoVenta.id);
    this.formulario.patchValue({
      sucursalId: puntoVenta.sucursalId,
      codigo: puntoVenta.codigo,
      puntoventa: puntoVenta.puntoventa,
      tipo: puntoVenta.tipo,
      estado: puntoVenta.estado,
    });
    this.modalAbierto.set(true);
  }

  protected cancelar(): void {
    this.modalAbierto.set(false);
    this.idEnEdicion.set(null);
    this.formulario.reset({
      sucursalId: this.sucursales()[0]?.id ?? 0,
      codigo: '',
      puntoventa: '',
      tipo: 5,
      estado: true,
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
    const solicitud: PuntoVentaSolicitud = {
      sucursalId: Number(valor.sucursalId),
      codigo: valor.codigo.trim(),
      puntoventa: valor.puntoventa.trim(),
      tipo: Number(valor.tipo),
      estado: valor.estado,
    };
    const id = this.idEnEdicion();
    const eraNuevo = id === null;

    this.guardando.set(true);

    const peticion = eraNuevo
      ? this.puntoVentaServicio.adicionar(solicitud)
      : this.puntoVentaServicio.modificar(id as number, solicitud);

    peticion.subscribe({
      next: () => {
        this.guardando.set(false);
        this.cancelar();
        this.toastServicio.exito(
          eraNuevo
            ? 'Punto de venta adicionado correctamente.'
            : 'Punto de venta modificado correctamente.',
        );
        this.listar();
      },
      error: (error: HttpErrorResponse) => {
        this.guardando.set(false);
        this.toastServicio.error(this.obtenerMensajeError(error));
      },
    });
  }

  protected async borrar(puntoVenta: PuntoVentaModelo): Promise<void> {
    const confirmado = await this.confirmacionServicio.confirmarBorrado(
      `${puntoVenta.codigo} - ${puntoVenta.puntoventa}`,
      'punto de venta',
    );
    if (!confirmado) {
      return;
    }

    this.cargando.set(true);

    this.puntoVentaServicio.eliminar(puntoVenta.id).subscribe({
      next: () => {
        this.cargando.set(false);
        this.toastServicio.exito('Punto de venta borrado correctamente.');
        this.listar();
      },
      error: (error: HttpErrorResponse) => {
        this.cargando.set(false);
        this.toastServicio.error(this.obtenerMensajeError(error));
      },
    });
  }

  protected obtenerCuis(puntoVenta: PuntoVentaModelo): void {
    this.obteniendoCuis.set(puntoVenta.id);

    this.puntoVentaServicio.obtenerCuis(puntoVenta.id).subscribe({
      next: (respuesta) => {
        this.obteniendoCuis.set(null);
        this.registrarCodigo(puntoVenta.id, { cuis: respuesta.cuis });
        this.toastServicio.exito(
          `CUIS ${respuesta.cuis} obtenido para el punto de venta ${respuesta.codigoPuntoVenta}.`,
        );
      },
      error: (error: HttpErrorResponse) => {
        this.obteniendoCuis.set(null);
        this.toastServicio.error(this.obtenerMensajeError(error));
      },
    });
  }

  protected obtenerCufd(puntoVenta: PuntoVentaModelo): void {
    this.obteniendoCufd.set(puntoVenta.id);

    this.puntoVentaServicio.obtenerCufd(puntoVenta.id).subscribe({
      next: (respuesta) => {
        this.obteniendoCufd.set(null);
        this.registrarCodigo(puntoVenta.id, { cufd: respuesta.cufd });
        this.toastServicio.exito(
          `CUFD ${respuesta.cufd} obtenido para el punto de venta ${respuesta.codigoPuntoVenta}.`,
        );
      },
      error: (error: HttpErrorResponse) => {
        this.obteniendoCufd.set(null);
        this.toastServicio.error(this.obtenerMensajeError(error));
      },
    });
  }

  protected sincronizarCatalogos(puntoVenta: PuntoVentaModelo): void {
    if (this.sincronizando() !== null) {
      return;
    }

    this.sincronizando.set(puntoVenta.id);
    this.catalogoServicio.sincronizar(puntoVenta.id).subscribe({
      next: (resumen) => {
        this.sincronizando.set(null);
        this.registrarCodigo(puntoVenta.id, { cuis: resumen.cuis });
        const mensaje =
          resumen.fallidas === 0
            ? `Sincronización finalizada: ${resumen.exitosas} exitosas, ${resumen.fallidas} fallidas.`
            : `Sincronización finalizada con fallas: ${resumen.exitosas} exitosas, ${resumen.fallidas} fallidas.`;
        if (resumen.fallidas === 0) {
          this.toastServicio.exito(mensaje);
        } else {
          this.toastServicio.advertencia(mensaje);
        }
      },
      error: (error: HttpErrorResponse) => {
        this.sincronizando.set(null);
        this.toastServicio.error(
          `No se pudo sincronizar el catálogo. ${this.obtenerMensajeError(error)}`,
        );
      },
    });
  }

  protected mostrarCatalogo(puntoVenta: PuntoVentaModelo): void {
    this.catalogoPuntoVenta.set(puntoVenta);
    this.catalogoModalAbierto.set(true);
    this.catalogoFunciones.set([]);
    this.catalogoFuncionSeleccionada.set('');
    this.catalogo.set(null);
    this.cargandoCatalogo.set(true);

    this.catalogoServicio.listarFunciones(puntoVenta.id).subscribe({
      next: (funciones) => {
        this.catalogoFunciones.set(funciones);
        const primeraFuncion = funciones[0]?.nombre ?? '';
        this.catalogoFuncionSeleccionada.set(primeraFuncion);
        if (primeraFuncion) {
          this.cargarCatalogo(puntoVenta.id, primeraFuncion);
        } else {
          this.cargandoCatalogo.set(false);
        }
      },
      error: (error: HttpErrorResponse) => {
        this.cargandoCatalogo.set(false);
        this.toastServicio.error(this.obtenerMensajeError(error));
      },
    });
  }

  protected cambiarCatalogo(funcion: string): void {
    const puntoVenta = this.catalogoPuntoVenta();
    if (!puntoVenta || !funcion) {
      return;
    }

    this.catalogoFuncionSeleccionada.set(funcion);
    this.cargarCatalogo(puntoVenta.id, funcion);
  }

  protected cerrarCatalogo(): void {
    this.catalogoModalAbierto.set(false);
    this.catalogoPuntoVenta.set(null);
    this.catalogoFunciones.set([]);
    this.catalogoFuncionSeleccionada.set('');
    this.catalogo.set(null);
    this.cargandoCatalogo.set(false);
  }

  private cargarCatalogo(puntoVentaId: number, funcion: string): void {
    this.cargandoCatalogo.set(true);
    this.catalogoServicio.obtener(puntoVentaId, funcion).subscribe({
      next: (catalogo) => {
        this.catalogo.set(catalogo);
        this.cargandoCatalogo.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.catalogo.set(null);
        this.cargandoCatalogo.set(false);
        this.toastServicio.error(this.obtenerMensajeError(error));
      },
    });
  }

  private cargarSucursales(): void {
    this.sucursalServicio.listar('', 0, 20).subscribe({
      next: (respuesta) => {
        this.sucursales.set(respuesta.content);
        const sucursalId = this.formulario.controls.sucursalId.value;
        if (sucursalId === 0 && respuesta.content.length > 0) {
          this.formulario.controls.sucursalId.setValue(respuesta.content[0].id);
        }
      },
      error: (error: HttpErrorResponse) => {
        this.toastServicio.error(this.obtenerMensajeError(error));
      },
    });
  }

  protected resumirCodigo(valor: string | null | undefined): string {
    if (!valor) {
      return '';
    }

    const longitud = 10;
    return valor.length > longitud ? `${valor.slice(0, longitud)}…` : valor;
  }

  private registrarCodigo(puntoVentaId: number, codigo: Partial<CodigosPuntoVenta>): void {
    this.codigosPorPuntoVenta.update((codigos) => ({
      ...codigos,
      [puntoVentaId]: { ...codigos[puntoVentaId], ...codigo },
    }));
  }

  private obtenerMensajeError(error: HttpErrorResponse): string {
    if (error.status === 0) {
      return 'No se pudo conectar con la parte lógica.';
    }

    if (typeof error.error === 'string') {
      return error.error;
    }

    const detalle = error.error?.detail;
    return typeof detalle === 'string' ? detalle : 'Ocurrió un error inesperado.';
  }
}
