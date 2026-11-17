package bo.org.citior.sife.servicio;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import bo.gob.impuesto.siat.codigos.MensajeServicio;
import bo.gob.impuesto.siat.codigos.RespuestaCuis;
import bo.gob.impuesto.siat.codigos.ServicioFacturacionCodigos;
import bo.gob.impuesto.siat.codigos.SolicitudCuis;
import bo.org.citior.sife.configuracion.PropiedadesSiat;
import bo.org.citior.sife.dominio.Empresa;
import bo.org.citior.sife.dominio.dto.CuisRespuesta;
import bo.org.citior.sife.dominio.dto.CuisSolicitud;
import bo.org.citior.sife.excepcion.ConfiguracionSiatInvalidaExcepcion;
import bo.org.citior.sife.excepcion.EmpresaNoEncontradaExcepcion;
import bo.org.citior.sife.excepcion.SiatNoDisponibleExcepcion;
import bo.org.citior.sife.excepcion.TokenSiatNoConfiguradoExcepcion;
import bo.org.citior.sife.repositorio.EmpresaRepositorio;
import bo.org.citior.sife.siat.ClienteSiatFactory;
import java.util.Optional;
import javax.xml.datatype.DatatypeFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ObtenerCuisServicioPrueba {

    @Mock
    private EmpresaRepositorio empresaRepositorio;

    @Mock
    private ClienteSiatFactory clienteSiatFactory;

    @Mock
    private ServicioFacturacionCodigos puerto;

    private ObtenerCuisServicio servicio;
    private Empresa empresa;
    private PropiedadesSiat propiedades;

    @BeforeEach
    void preparar() {
        propiedades = new PropiedadesSiat(
                "https://example.test/codigos", null, null, null, 5000, 15000);
        servicio = new ObtenerCuisServicio(
                empresaRepositorio, clienteSiatFactory, propiedades);
        empresa = Empresa.builder()
                .id(1L)
                .nit("710035027")
                .codigoSistema("SISTEMA")
                .modalidad("1")
                .ambiente("2")
                .token("token-de-prueba")
                .build();
    }

    @ParameterizedTest
    @ValueSource(ints = {0, 1})
    void obtieneCuisParaPuntoDeVenta(int codigoPuntoVenta) throws Exception {
        given(empresaRepositorio.findById(1L)).willReturn(Optional.of(empresa));
        given(clienteSiatFactory.crearPuerto(
                eq(ServicioFacturacionCodigos.class),
                eq(propiedades.codigosUrl()),
                eq("token-de-prueba")))
                .willReturn(puerto);
        given(puerto.cuis(any(SolicitudCuis.class))).willReturn(respuestaSiat());

        CuisRespuesta respuesta = servicio.obtener(1L, new CuisSolicitud(codigoPuntoVenta));

        ArgumentCaptor<SolicitudCuis> captor = ArgumentCaptor.forClass(SolicitudCuis.class);
        verify(puerto).cuis(captor.capture());
        SolicitudCuis solicitudSiat = captor.getValue();

        assertThat(solicitudSiat.getCodigoAmbiente()).isEqualTo(2);
        assertThat(solicitudSiat.getCodigoModalidad()).isEqualTo(1);
        assertThat(solicitudSiat.getCodigoSistema()).isEqualTo("SISTEMA");
        assertThat(solicitudSiat.getCodigoSucursal()).isZero();
        assertThat(solicitudSiat.getNit()).isEqualTo(710035027L);
        assertThat(solicitudSiat.getCodigoPuntoVenta().getValue())
                .isEqualTo(codigoPuntoVenta);

        assertThat(respuesta.empresaId()).isEqualTo(1L);
        assertThat(respuesta.nit()).isEqualTo("710035027");
        assertThat(respuesta.codigoSucursal()).isZero();
        assertThat(respuesta.codigoPuntoVenta()).isEqualTo(codigoPuntoVenta);
        assertThat(respuesta.cuis()).isEqualTo("CUIS-123");
        assertThat(respuesta.fechaVigencia())
                .isEqualTo("2027-05-16T23:59:59.000-04:00");
        assertThat(respuesta.transaccion()).isTrue();
        assertThat(respuesta.mensajes()).containsExactly(
                new CuisRespuesta.MensajeCuis(926, "COMUNICACION EXITOSA"));
    }

    @Test
    void rechazaEmpresaInexistente() {
        given(empresaRepositorio.findById(1L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> servicio.obtener(1L, new CuisSolicitud(0)))
                .isInstanceOf(EmpresaNoEncontradaExcepcion.class);

        verifyNoInteractions(clienteSiatFactory);
    }

    @Test
    void rechazaEmpresaSinToken() {
        empresa.setToken(" ");
        given(empresaRepositorio.findById(1L)).willReturn(Optional.of(empresa));

        assertThatThrownBy(() -> servicio.obtener(1L, new CuisSolicitud(0)))
                .isInstanceOf(TokenSiatNoConfiguradoExcepcion.class);

        verifyNoInteractions(clienteSiatFactory);
    }

    @Test
    void rechazaConfiguracionSiatInvalida() {
        empresa.setAmbiente("PRODUCCION");
        given(empresaRepositorio.findById(1L)).willReturn(Optional.of(empresa));

        assertThatThrownBy(() -> servicio.obtener(1L, new CuisSolicitud(0)))
                .isInstanceOf(ConfiguracionSiatInvalidaExcepcion.class)
                .hasMessageContaining("ambiente");

        verifyNoInteractions(clienteSiatFactory);
    }

    @Test
    void informaSiatNoDisponible() {
        given(empresaRepositorio.findById(1L)).willReturn(Optional.of(empresa));
        given(clienteSiatFactory.crearPuerto(
                eq(ServicioFacturacionCodigos.class),
                eq(propiedades.codigosUrl()),
                eq("token-de-prueba")))
                .willReturn(puerto);
        given(puerto.cuis(any(SolicitudCuis.class)))
                .willThrow(new RuntimeException("timeout"));

        assertThatThrownBy(() -> servicio.obtener(1L, new CuisSolicitud(0)))
                .isInstanceOf(SiatNoDisponibleExcepcion.class)
                .hasMessageContaining("timeout");
    }

    private RespuestaCuis respuestaSiat() throws Exception {
        RespuestaCuis respuesta = new RespuestaCuis();
        respuesta.setCodigo("CUIS-123");
        respuesta.setFechaVigencia(DatatypeFactory.newInstance()
                .newXMLGregorianCalendar("2027-05-16T23:59:59.000-04:00"));
        respuesta.setTransaccion(true);

        MensajeServicio mensaje = new MensajeServicio();
        mensaje.setCodigo(926);
        mensaje.setDescripcion("COMUNICACION EXITOSA");
        respuesta.getMensajesList().add(mensaje);

        return respuesta;
    }
}
