import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { forkJoin } from 'rxjs';
import { BotonIcono } from '../../compartido/boton-icono';
import { Modal } from '../../compartido/modal';
import {
  ActividadOpcion,
  Producto as ProductoModelo,
  ProductoServicioOpcion,
  ProductoSolicitud,
  UnidadMedidaOpcion,
} from '../../modelos/producto';
import { ConfirmacionServicio } from '../../servicios/confirmacion.servicio';
import { ProductoServicio } from '../../servicios/producto.servicio';
import { ToastServicio } from '../../servicios/toast.servicio';

@Component({
  selector: 'sife-producto',
  imports: [ReactiveFormsModule, BotonIcono, Modal],
  templateUrl: './producto.html',
})
export class Producto implements OnInit {
  private readonly constructorFormulario = inject(FormBuilder);
  private readonly productoServicio = inject(ProductoServicio);
  private readonly toastServicio = inject(ToastServicio);
  private readonly confirmacionServicio = inject(ConfirmacionServicio);

  protected readonly productos = signal<ProductoModelo[]>([]);
  protected readonly actividades = signal<ActividadOpcion[]>([]);
  protected readonly productosServicio = signal<ProductoServicioOpcion[]>([]);
  protected readonly unidadesMedida = signal<UnidadMedidaOpcion[]>([]);
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
    this.idEnEdicion() === null ? 'Adicionar producto' : 'Editar producto',
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
    producto: ['', [Validators.required, Validators.maxLength(250)]],
    precioUnitario: [0, [Validators.required, Validators.min(0)]],
    codigoActividad: ['', [Validators.required]],
    codigoProducto: [0, [Validators.required]],
    unidadMedida: [0, [Validators.required]],
  });

  ngOnInit(): void {
    this.cargarCatalogos();
    this.listar();
  }

  protected listar(): void {
    this.cargando.set(true);

    this.productoServicio
      .listar(this.busqueda(), this.pagina(), this.tamano())
      .subscribe({
        next: (respuesta) => {
          this.productos.set(respuesta.content);
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
      producto: '',
      precioUnitario: 0,
      codigoActividad: this.actividades()[0]?.codigo ?? '',
      codigoProducto: this.productosServicio()[0]?.codigo ?? 0,
      unidadMedida: this.unidadesMedida()[0]?.codigo ?? 0,
    });
    this.modalAbierto.set(true);
  }

  protected editar(producto: ProductoModelo): void {
    this.idEnEdicion.set(producto.id);
    this.formulario.patchValue({
      codigoInterno: producto.codigoInterno,
      producto: producto.producto,
      precioUnitario: producto.precioUnitario,
      codigoActividad: producto.codigoActividad,
      codigoProducto: producto.codigoProducto,
      unidadMedida: producto.unidadMedida,
    });
    this.modalAbierto.set(true);
  }

  protected cancelar(): void {
    this.modalAbierto.set(false);
    this.idEnEdicion.set(null);
    this.formulario.reset({
      codigoInterno: '',
      producto: '',
      precioUnitario: 0,
      codigoActividad: this.actividades()[0]?.codigo ?? '',
      codigoProducto: this.productosServicio()[0]?.codigo ?? 0,
      unidadMedida: this.unidadesMedida()[0]?.codigo ?? 0,
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
    const solicitud: ProductoSolicitud = {
      codigoInterno: valor.codigoInterno.trim(),
      producto: valor.producto.trim(),
      precioUnitario: Number(valor.precioUnitario),
      codigoActividad: valor.codigoActividad.trim(),
      codigoProducto: Number(valor.codigoProducto),
      unidadMedida: Number(valor.unidadMedida),
    };
    const id = this.idEnEdicion();
    const eraNuevo = id === null;

    this.guardando.set(true);

    const peticion = eraNuevo
      ? this.productoServicio.adicionar(solicitud)
      : this.productoServicio.modificar(id as number, solicitud);

    peticion.subscribe({
      next: () => {
        this.guardando.set(false);
        this.cancelar();
        this.toastServicio.exito(
          eraNuevo
            ? 'Producto adicionado correctamente.'
            : 'Producto modificado correctamente.',
        );
        this.listar();
      },
      error: (error: HttpErrorResponse) => {
        this.guardando.set(false);
        this.toastServicio.error(this.obtenerMensajeError(error));
      },
    });
  }

  protected async borrar(producto: ProductoModelo): Promise<void> {
    const confirmado = await this.confirmacionServicio.confirmarBorrado(
      producto.producto,
      'producto',
    );
    if (!confirmado) {
      return;
    }

    this.cargando.set(true);

    this.productoServicio.eliminar(producto.id).subscribe({
      next: () => {
        this.cargando.set(false);
        this.toastServicio.exito('Producto borrado correctamente.');
        this.listar();
      },
      error: (error: HttpErrorResponse) => {
        this.cargando.set(false);
        this.toastServicio.error(this.obtenerMensajeError(error));
      },
    });
  }

  protected formatoPrecio(valor: number): string {
    return valor.toFixed(2);
  }

  private cargarCatalogos(): void {
    forkJoin({
      actividades: this.productoServicio.listarActividades(),
      productos: this.productoServicio.listarProductosServicio(),
      unidades: this.productoServicio.listarUnidadesMedida(),
    }).subscribe({
      next: ({ actividades, productos, unidades }) => {
        this.actividades.set(actividades);
        this.productosServicio.set(productos);
        this.unidadesMedida.set(unidades);

        const valores = this.formulario.getRawValue();
        if (actividades.length > 0 && !actividades.some((item) => item.codigo === valores.codigoActividad)) {
          this.formulario.controls.codigoActividad.setValue(actividades[0].codigo);
        }
        if (productos.length > 0 && !productos.some((item) => item.codigo === valores.codigoProducto)) {
          this.formulario.controls.codigoProducto.setValue(productos[0].codigo);
        }
        if (unidades.length > 0 && !unidades.some((item) => item.codigo === valores.unidadMedida)) {
          this.formulario.controls.unidadMedida.setValue(unidades[0].codigo);
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
