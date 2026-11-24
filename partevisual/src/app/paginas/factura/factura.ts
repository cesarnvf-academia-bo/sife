import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { BotonIcono } from '../../compartido/boton-icono';
import { Modal } from '../../compartido/modal';
import { CatalogoFactura, Factura as FacturaModelo } from '../../modelos/factura';
import { ConfirmacionServicio } from '../../servicios/confirmacion.servicio';
import { FacturaServicio } from '../../servicios/factura.servicio';
import { ToastServicio } from '../../servicios/toast.servicio';

@Component({
  selector: 'sife-factura',
  imports: [ReactiveFormsModule, BotonIcono, Modal],
  templateUrl: './factura.html',
})
export class Factura implements OnInit {
  private readonly constructorFormulario = inject(FormBuilder);
  private readonly facturaServicio = inject(FacturaServicio);
  private readonly toastServicio = inject(ToastServicio);
  private readonly confirmacionServicio = inject(ConfirmacionServicio);

  protected readonly facturas = signal<FacturaModelo[]>([]);
  protected readonly catalogos = signal<CatalogoFactura>({
    sucursales: [],
    puntosVenta: [],
    clientes: [],
    tiposDocumentoIdentidad: [],
    metodosPago: [],
    monedas: [],
    motivosAnulacion: [],
  });
  protected readonly busqueda = signal('');
  protected readonly tamano = signal(5);
  protected readonly pagina = signal(0);
  protected readonly total = signal(0);
  protected readonly totalPaginas = signal(0);
  protected readonly cargando = signal(false);
  protected readonly guardando = signal(false);
  protected readonly modalAbierto = signal(false);
  protected readonly modalAnulacionAbierto = signal(false);
  protected readonly facturaEnAnulacion = signal<FacturaModelo | null>(null);

  protected readonly tituloModal = computed(() => 'Emitir factura');
  protected readonly tituloAnulacion = computed(
    () => `Anular factura N° ${this.facturaEnAnulacion()?.numeroFactura ?? ''}`,
  );
  protected readonly desde = computed(() =>
    this.total() === 0 ? 0 : this.pagina() * this.tamano() + 1,
  );
  protected readonly esPrimera = computed(() => this.pagina() === 0);
  protected readonly esUltima = computed(
    () => this.totalPaginas() === 0 || this.pagina() >= this.totalPaginas() - 1,
  );

  protected readonly formulario = this.constructorFormulario.nonNullable.group({
    sucursalId: ['', [Validators.required]],
    puntoventaId: [''],
    clienteId: ['', [Validators.required]],
    codigoAmbiente: [1, [Validators.required]],
    codigoEmision: [1, [Validators.required]],
    codigoModalidad: [1, [Validators.required]],
    codigoMetodoPago: ['', [Validators.required]],
    tipoFacturaDocumento: [1, [Validators.required]],
    codigoTipoDocumentoIdentidad: ['', [Validators.required]],
    nombreRazonSocial: ['', [Validators.required, Validators.maxLength(250)]],
    numeroDocumento: ['', [Validators.required, Validators.maxLength(50)]],
    complemento: [''],
    municipio: ['', [Validators.required]],
    direccion: ['', [Validators.required]],
    telefono: [''],
    numeroTarjeta: [''],
    montoGiftCard: [''],
    descuentoAdicional: [''],
    codigoExcepcion: [''],
    cafc: [''],
    codigoMoneda: ['', [Validators.required]],
    tipoCambio: [1, [Validators.required, Validators.min(0.000001)]],
    leyenda: ['', [Validators.required]],
    usuario: ['', [Validators.required, Validators.maxLength(100)]],
    detalle: this.constructorFormulario.nonNullable.array([this.crearLinea()]),
  });

  protected readonly anulacionForm = this.constructorFormulario.nonNullable.group({
    codigoMotivo: ['', [Validators.required]],
  });

  protected get lineas() {
    return this.formulario.controls.detalle.controls;
  }

  ngOnInit(): void {
    this.formulario.controls.detalle.valueChanges.subscribe(() => this.recalcularSubtotales());
    this.cargarCatalogos();
    this.listar('', 0, 5);
  }

  protected listar(busqueda = this.busqueda(), page = this.pagina(), size = this.tamano()): void {
    this.cargando.set(true);

    this.facturaServicio.listar(busqueda, page, size).subscribe({
      next: (respuesta) => {
        this.facturas.set(respuesta.content);
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

  protected emitir(): void {
    this.reiniciarFormulario();
    this.modalAbierto.set(true);
  }

  protected cancelar(): void {
    this.modalAbierto.set(false);
    this.reiniciarFormulario();
  }

  protected agregarLinea(): void {
    this.formulario.controls.detalle.push(this.crearLinea());
  }

  protected quitarLinea(): void {
    if (this.lineas.length > 1) {
      this.formulario.controls.detalle.removeAt(this.lineas.length - 1);
      this.recalcularSubtotales();
    }
  }

  protected guardar(): void {
    if (this.guardando()) {
      return;
    }

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      this.toastServicio.advertencia('Complete los campos obligatorios de la factura.');
      return;
    }

    const valor = this.formulario.getRawValue();
    const solicitud = {
      sucursalId: this.numero(valor.sucursalId),
      puntoventaId: this.numero(valor.puntoventaId),
      clienteId: this.numero(valor.clienteId),
      codigoAmbiente: this.numero(valor.codigoAmbiente),
      codigoEmision: this.numero(valor.codigoEmision),
      codigoModalidad: this.numero(valor.codigoModalidad),
      codigoMetodoPago: this.numero(valor.codigoMetodoPago),
      tipoFacturaDocumento: this.numero(valor.tipoFacturaDocumento),
      fechaEmision: new Date().toISOString(),
      codigoTipoDocumentoIdentidad: this.numero(valor.codigoTipoDocumentoIdentidad),
      nombreRazonSocial: valor.nombreRazonSocial.trim(),
      numeroDocumento: valor.numeroDocumento.trim(),
      complemento: this.texto(valor.complemento),
      municipio: valor.municipio.trim(),
      direccion: valor.direccion.trim(),
      telefono: this.texto(valor.telefono),
      numeroTarjeta: this.texto(valor.numeroTarjeta),
      montoGiftCard: this.numero(valor.montoGiftCard),
      descuentoAdicional: this.numero(valor.descuentoAdicional),
      codigoExcepcion: this.numero(valor.codigoExcepcion),
      cafc: this.texto(valor.cafc),
      codigoMoneda: this.numero(valor.codigoMoneda),
      tipoCambio: Number(valor.tipoCambio),
      leyenda: valor.leyenda.trim(),
      usuario: valor.usuario.trim(),
      detalle: valor.detalle.map((linea) => ({
        actividadEconomica: linea.actividadEconomica.trim(),
        codigoProductoSin: this.numero(linea.codigoProductoSin),
        codigoProducto: String(linea.codigoProducto).trim(),
        descripcion: linea.descripcion.trim(),
        cantidad: this.numero(linea.cantidad),
        unidadMedida: this.numero(linea.unidadMedida),
        precioUnitario: this.numero(linea.precioUnitario),
        montoDescuento: this.numero(linea.montoDescuento),
        subTotal: this.numero(linea.subTotal),
        numeroSerie: this.texto(linea.numeroSerie),
        numeroImei: this.texto(linea.numeroImei),
      })),
    };

    this.guardando.set(true);

    this.facturaServicio.emitir(solicitud).subscribe({
      next: () => {
        this.guardando.set(false);
        this.cancelar();
        this.toastServicio.exito('Factura emitida correctamente.');
        this.listar();
      },
      error: (error: HttpErrorResponse) => {
        this.guardando.set(false);
        this.toastServicio.error(this.obtenerMensajeError(error));
      },
    });
  }

  protected anular(factura: FacturaModelo): void {
    this.facturaEnAnulacion.set(factura);
    this.anulacionForm.reset({
      codigoMotivo: this.catalogos().motivosAnulacion[0]?.codigo ?? '',
    });
    this.modalAnulacionAbierto.set(true);
  }

  protected cancelarAnulacion(): void {
    this.modalAnulacionAbierto.set(false);
    this.facturaEnAnulacion.set(null);
    this.anulacionForm.reset({ codigoMotivo: '' });
  }

  protected async confirmarAnulacion(): Promise<void> {
    const factura = this.facturaEnAnulacion();
    if (!factura || this.anulacionForm.invalid) {
      this.anulacionForm.markAllAsTouched();
      this.toastServicio.advertencia('Seleccione el motivo de anulación.');
      return;
    }

    const confirmado = await this.confirmacionServicio.confirmar(
      '¿Anular factura?',
      `Se anulará la factura N° ${factura.numeroFactura}. Esta acción se puede revertir.`,
      'Sí, anular',
      'factura',
    );
    if (!confirmado) {
      return;
    }

    const codigoMotivo = Number(this.anulacionForm.controls.codigoMotivo.value);
    this.guardando.set(true);

    this.facturaServicio.anular(factura.id, codigoMotivo).subscribe({
      next: () => {
        this.guardando.set(false);
        this.cancelarAnulacion();
        this.toastServicio.exito('Factura anulada correctamente.');
        this.listar();
      },
      error: (error: HttpErrorResponse) => {
        this.guardando.set(false);
        this.toastServicio.error(this.obtenerMensajeError(error));
      },
    });
  }

  protected async revertir(factura: FacturaModelo): Promise<void> {
    const confirmado = await this.confirmacionServicio.confirmar(
      '¿Revertir anulación?',
      `Se revertirá la anulación de la factura N° ${factura.numeroFactura}.`,
      'Sí, revertir',
      'factura',
    );
    if (!confirmado) {
      return;
    }

    this.cargando.set(true);

    this.facturaServicio.revertir(factura.id).subscribe({
      next: () => {
        this.cargando.set(false);
        this.toastServicio.exito('Anulación revertida correctamente.');
        this.listar();
      },
      error: (error: HttpErrorResponse) => {
        this.cargando.set(false);
        this.toastServicio.error(this.obtenerMensajeError(error));
      },
    });
  }

  protected verXml(factura: FacturaModelo): void {
    this.facturaServicio.verXml(factura.id).subscribe({
      next: (blob) => {
        const url = URL.createObjectURL(blob);
        const enlace = document.createElement('a');
        enlace.href = url;
        enlace.download = `factura-${factura.numeroFactura}.xml`;
        document.body.appendChild(enlace);
        enlace.click();
        enlace.remove();
        URL.revokeObjectURL(url);
        this.toastServicio.exito('XML de la factura descargado correctamente.');
      },
      error: (error: HttpErrorResponse) => {
        this.toastServicio.error(this.obtenerMensajeError(error));
      },
    });
  }

  protected formatoMonto(valor: number): string {
    return valor.toFixed(2);
  }

  private crearLinea() {
    return this.constructorFormulario.nonNullable.group({
      actividadEconomica: ['', [Validators.required]],
      codigoProductoSin: [0, [Validators.required, Validators.min(1)]],
      codigoProducto: ['', [Validators.required]],
      descripcion: ['', [Validators.required]],
      cantidad: [1, [Validators.required, Validators.min(0.000001)]],
      unidadMedida: [1, [Validators.required, Validators.min(1)]],
      precioUnitario: [0, [Validators.required, Validators.min(0.000001)]],
      montoDescuento: [0],
      subTotal: [0],
      numeroSerie: [''],
      numeroImei: [''],
    });
  }

  private reiniciarFormulario(): void {
    const catalogos = this.catalogos();
    this.formulario.reset({
      sucursalId: catalogos.sucursales[0]?.codigo ?? '',
      puntoventaId: '',
      clienteId: catalogos.clientes[0]?.codigo ?? '',
      codigoAmbiente: 1,
      codigoEmision: 1,
      codigoModalidad: 1,
      codigoMetodoPago: catalogos.metodosPago[0]?.codigo ?? '',
      tipoFacturaDocumento: 1,
      codigoTipoDocumentoIdentidad: catalogos.tiposDocumentoIdentidad[0]?.codigo ?? '',
      nombreRazonSocial: '',
      numeroDocumento: '',
      complemento: '',
      municipio: '',
      direccion: '',
      telefono: '',
      numeroTarjeta: '',
      montoGiftCard: '',
      descuentoAdicional: '',
      codigoExcepcion: '',
      cafc: '',
      codigoMoneda: catalogos.monedas[0]?.codigo ?? '',
      tipoCambio: 1,
      leyenda: '',
      usuario: '',
      detalle: [this.crearLinea().getRawValue()],
    });
    this.recalcularSubtotales();
  }

  private cargarCatalogos(): void {
    this.facturaServicio.catalogos().subscribe({
      next: (respuesta) => {
        this.catalogos.set(respuesta);
        this.reiniciarFormulario();
        if (
          respuesta.motivosAnulacion.length > 0 &&
          !respuesta.motivosAnulacion.some(
            (opcion) => opcion.codigo === this.anulacionForm.controls.codigoMotivo.value,
          )
        ) {
          this.anulacionForm.controls.codigoMotivo.setValue(respuesta.motivosAnulacion[0].codigo);
        }
      },
      error: (error: HttpErrorResponse) => {
        this.toastServicio.error(this.obtenerMensajeError(error));
      },
    });
  }

  private recalcularSubtotales(): void {
    for (const linea of this.formulario.controls.detalle.controls) {
      const cantidad = Number(linea.controls.cantidad.value) || 0;
      const precio = Number(linea.controls.precioUnitario.value) || 0;
      const descuento = Number(linea.controls.montoDescuento.value) || 0;
      linea.controls.subTotal.setValue(Number((cantidad * precio - descuento).toFixed(2)), {
        emitEvent: false,
      });
    }
  }

  private numero(valor: number | string | null | undefined): number | null {
    if (valor === null || valor === undefined || String(valor).trim() === '') {
      return null;
    }
    const resultado = Number(valor);
    return Number.isFinite(resultado) ? resultado : null;
  }

  private texto(valor: string | null | undefined): string | null {
    if (valor === null || valor === undefined || valor.trim() === '') {
      return null;
    }
    return valor.trim();
  }

  private obtenerMensajeError(error: HttpErrorResponse): string {
    if (error.status === 0) {
      return 'No se pudo conectar con la parte lógica.';
    }

    if (typeof error.error === 'string') {
      return error.error;
    }

    const errores = (error.error?.errores ?? error.error?.errors) as
      | Record<string, unknown>
      | undefined;
    if (errores) {
      const primero = Object.values(errores)[0];
      if (Array.isArray(primero) && typeof primero[0] === 'string') {
        return primero[0];
      }
      if (typeof primero === 'string') {
        return primero;
      }
    }

    const detalle = error.error?.detail;
    return typeof detalle === 'string' ? detalle : 'Ocurrió un error inesperado.';
  }
}
