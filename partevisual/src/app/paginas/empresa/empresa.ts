import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { BotonIcono } from '../../compartido/boton-icono';
import { Empresa as EmpresaModelo, EmpresaSolicitud } from '../../modelos/empresa';
import { EmpresaServicio } from '../../servicios/empresa.servicio';

@Component({
  selector: 'sife-empresa',
  imports: [ReactiveFormsModule, BotonIcono],
  templateUrl: './empresa.html',
})
export class Empresa {
  private readonly constructorFormulario = inject(FormBuilder);
  private readonly empresaServicio = inject(EmpresaServicio);

  protected readonly cargando = signal(false);
  protected readonly mensaje = signal('');
  protected readonly error = signal('');
  protected readonly tieneToken = signal(false);
  protected readonly tieneClaveFirmaDigital = signal(false);

  protected readonly formulario = this.constructorFormulario.nonNullable.group({
    id: [''],
    nit: ['', [Validators.required, Validators.maxLength(20)]],
    razonsocial: ['', [Validators.required, Validators.maxLength(250)]],
    codigoSistema: ['', [Validators.maxLength(50)]],
    modalidad: ['', [Validators.maxLength(50)]],
    ambiente: ['', [Validators.maxLength(50)]],
    rutaCodigo: [''],
    rutaSincronizacion: [''],
    rutaOperacion: [''],
    rutaCompraventa: [''],
    token: [''],
    rutaFirmaDigital: [''],
    claveFirmaDigital: [''],
  });

  protected buscar(): void {
    if (this.cargando()) {
      return;
    }

    const id = this.obtenerId();
    if (id === null) {
      this.mostrarError('Ingrese un identificador válido.');
      return;
    }

    this.cargando.set(true);
    this.limpiarMensajes();

    this.empresaServicio.obtenerPorId(id).subscribe({
      next: (empresa) => {
        this.cargarEmpresa(empresa);
        this.mensaje.set('Empresa consultada correctamente.');
        this.cargando.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.mostrarError(this.obtenerMensajeError(error));
        this.cargando.set(false);
      },
    });
  }

  protected adicionar(): void {
    if (this.cargando()) {
      return;
    }

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      this.mostrarError('Complete los campos obligatorios.');
      return;
    }

    this.cargando.set(true);
    this.limpiarMensajes();

    this.empresaServicio.adicionar(this.construirSolicitud()).subscribe({
      next: (empresa) => {
        this.cargarEmpresa(empresa);
        this.mensaje.set('Empresa adicionada correctamente.');
        this.cargando.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.mostrarError(this.obtenerMensajeError(error));
        this.cargando.set(false);
      },
    });
  }

  protected modificar(): void {
    if (this.cargando()) {
      return;
    }

    const id = this.obtenerId();
    if (id === null) {
      this.mostrarError('Consulte una empresa antes de modificarla.');
      return;
    }

    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      this.mostrarError('Complete los campos obligatorios.');
      return;
    }

    this.cargando.set(true);
    this.limpiarMensajes();

    this.empresaServicio.modificar(id, this.construirSolicitud()).subscribe({
      next: (empresa) => {
        this.cargarEmpresa(empresa);
        this.mensaje.set('Empresa modificada correctamente.');
        this.cargando.set(false);
      },
      error: (error: HttpErrorResponse) => {
        this.mostrarError(this.obtenerMensajeError(error));
        this.cargando.set(false);
      },
    });
  }

  protected limpiar(): void {
    this.formulario.reset();
    this.tieneToken.set(false);
    this.tieneClaveFirmaDigital.set(false);
    this.limpiarMensajes();
  }

  private cargarEmpresa(empresa: EmpresaModelo): void {
    this.formulario.patchValue({
      id: String(empresa.id),
      nit: empresa.nit ?? '',
      razonsocial: empresa.razonsocial ?? '',
      codigoSistema: empresa.codigoSistema ?? '',
      modalidad: empresa.modalidad ?? '',
      ambiente: empresa.ambiente ?? '',
      rutaCodigo: empresa.rutaCodigo ?? '',
      rutaSincronizacion: empresa.rutaSincronizacion ?? '',
      rutaOperacion: empresa.rutaOperacion ?? '',
      rutaCompraventa: empresa.rutaCompraventa ?? '',
      token: '',
      rutaFirmaDigital: empresa.rutaFirmaDigital ?? '',
      claveFirmaDigital: '',
    });

    this.tieneToken.set(empresa.tieneToken);
    this.tieneClaveFirmaDigital.set(empresa.tieneClaveFirmaDigital);
  }

  private construirSolicitud(): EmpresaSolicitud {
    const valor = this.formulario.getRawValue();

    return {
      nit: valor.nit.trim(),
      razonsocial: valor.razonsocial.trim(),
      codigoSistema: this.normalizar(valor.codigoSistema),
      modalidad: this.normalizar(valor.modalidad),
      ambiente: this.normalizar(valor.ambiente),
      rutaCodigo: this.normalizar(valor.rutaCodigo),
      rutaSincronizacion: this.normalizar(valor.rutaSincronizacion),
      rutaOperacion: this.normalizar(valor.rutaOperacion),
      rutaCompraventa: this.normalizar(valor.rutaCompraventa),
      token: this.normalizar(valor.token),
      rutaFirmaDigital: this.normalizar(valor.rutaFirmaDigital),
      claveFirmaDigital: this.normalizar(valor.claveFirmaDigital),
    };
  }

  private obtenerId(): number | null {
    const id = Number(this.formulario.controls.id.value);
    return Number.isInteger(id) && id > 0 ? id : null;
  }

  private normalizar(valor: string): string | null {
    const limpio = valor.trim();
    return limpio.length > 0 ? limpio : null;
  }

  private mostrarError(mensaje: string): void {
    this.mensaje.set('');
    this.error.set(mensaje);
  }

  private limpiarMensajes(): void {
    this.mensaje.set('');
    this.error.set('');
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
