import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, computed, inject, signal } from '@angular/core';
import { FormBuilder, ReactiveFormsModule, Validators } from '@angular/forms';
import { BotonIcono } from '../../compartido/boton-icono';
import { Modal } from '../../compartido/modal';
import { Empresa as EmpresaModelo, EmpresaSolicitud } from '../../modelos/empresa';
import { EmpresaServicio } from '../../servicios/empresa.servicio';
import { ToastServicio } from '../../servicios/toast.servicio';

@Component({
  selector: 'sife-empresa',
  imports: [ReactiveFormsModule, BotonIcono, Modal],
  templateUrl: './empresa.html',
})
export class Empresa implements OnInit {
  private readonly constructorFormulario = inject(FormBuilder);
  private readonly empresaServicio = inject(EmpresaServicio);
  private readonly toastServicio = inject(ToastServicio);

  protected readonly cargando = signal(false);
  protected readonly guardando = signal(false);
  protected readonly modalAbierto = signal(false);
  protected readonly existeRegistro = signal(false);
  protected readonly empresa = signal<EmpresaModelo | null>(null);
  protected readonly tieneToken = signal(false);
  protected readonly tieneClaveFirmaDigital = signal(false);

  protected readonly tituloModal = computed(() =>
    this.existeRegistro() ? 'Editar empresa' : 'Adicionar empresa',
  );
  protected readonly textoAccion = computed(() =>
    this.existeRegistro() ? 'Editar' : 'Adicionar',
  );
  protected readonly iconoAccion = computed(() =>
    this.existeRegistro() ? 'editar' : 'agregar',
  );

  protected readonly formulario = this.constructorFormulario.nonNullable.group({
    id: ['1'],
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

  ngOnInit(): void {
    this.cargarEmpresa();
  }

  protected abrirFormulario(): void {
    if (!this.existeRegistro()) {
      this.formulario.reset({
        id: '1',
        nit: '',
        razonsocial: '',
        codigoSistema: '',
        modalidad: '',
        ambiente: '',
        rutaCodigo: '',
        rutaSincronizacion: '',
        rutaOperacion: '',
        rutaCompraventa: '',
        token: '',
        rutaFirmaDigital: '',
        claveFirmaDigital: '',
      });
    } else {
      this.formulario.patchValue({ token: '', claveFirmaDigital: '' });
    }
    this.modalAbierto.set(true);
  }

  protected cerrarFormulario(): void {
    this.modalAbierto.set(false);
    this.formulario.patchValue({ token: '', claveFirmaDigital: '' });
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

    const eraNuevo = !this.existeRegistro();
    this.guardando.set(true);
    const solicitud = this.construirSolicitud();

    const peticion = eraNuevo
      ? this.empresaServicio.adicionar(solicitud)
      : this.empresaServicio.modificar(1, solicitud);

    peticion.subscribe({
      next: (respuesta) => {
        this.existeRegistro.set(true);
        this.empresa.set(respuesta);
        this.cargarDatos(respuesta);
        this.guardando.set(false);
        this.cerrarFormulario();
        this.toastServicio.exito(
          eraNuevo
            ? 'Empresa adicionada correctamente.'
            : 'Empresa modificada correctamente.',
        );
      },
      error: (error: HttpErrorResponse) => {
        this.guardando.set(false);
        this.toastServicio.error(this.obtenerMensajeError(error));
      },
    });
  }

  private cargarEmpresa(): void {
    this.cargando.set(true);

    this.empresaServicio.obtenerPorId(1).subscribe({
      next: (respuesta) => {
        this.existeRegistro.set(true);
        this.empresa.set(respuesta);
        this.cargarDatos(respuesta);
        this.cargando.set(false);
        this.toastServicio.informacion('Empresa cargada correctamente.');
      },
      error: (error: HttpErrorResponse) => {
        if (error.status === 404) {
          this.existeRegistro.set(false);
          this.empresa.set(null);
          this.formulario.patchValue({ id: '1' });
          this.tieneToken.set(false);
          this.tieneClaveFirmaDigital.set(false);
          this.toastServicio.informacion('No hay empresa registrada. Adiciónela para continuar.');
        } else {
          this.toastServicio.error(this.obtenerMensajeError(error));
        }
        this.cargando.set(false);
      },
    });
  }

  private cargarDatos(empresa: EmpresaModelo): void {
    this.formulario.patchValue({
      id: '1',
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

  private normalizar(valor: string): string | null {
    const limpio = valor.trim();
    return limpio.length > 0 ? limpio : null;
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
