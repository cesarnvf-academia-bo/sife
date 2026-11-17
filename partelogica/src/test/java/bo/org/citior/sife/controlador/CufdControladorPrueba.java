package bo.org.citior.sife.controlador;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bo.org.citior.sife.dominio.dto.CufdRespuesta;
import bo.org.citior.sife.dominio.dto.CufdSolicitud;
import bo.org.citior.sife.excepcion.ConfiguracionSiatInvalidaExcepcion;
import bo.org.citior.sife.excepcion.EmpresaNoEncontradaExcepcion;
import bo.org.citior.sife.excepcion.SiatNoDisponibleExcepcion;
import bo.org.citior.sife.excepcion.TokenSiatNoConfiguradoExcepcion;
import bo.org.citior.sife.servicio.ObtenerCufdServicio;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(CufdControlador.class)
class CufdControladorPrueba {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ObtenerCufdServicio obtenerCufdServicio;

    @BeforeEach
    void reiniciarSimulacion() {
        reset(obtenerCufdServicio);
    }

    @Test
    void respondeCufd() throws Exception {
        CufdRespuesta respuesta = new CufdRespuesta(
                1L,
                "710035027",
                0,
                0,
                "CUFD-123",
                "CTRL-456",
                "LA PAZ",
                "2026-11-17T23:59:59.000-04:00",
                true,
                List.of(new CufdRespuesta.MensajeCufd(926, "COMUNICACION EXITOSA")));
        given(obtenerCufdServicio.obtener(1L, new CufdSolicitud(0))).willReturn(respuesta);

        mockMvc.perform(post("/api/v1/empresas/1/cufd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoPuntoVenta\":0}"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.empresaId").value(1))
                .andExpect(jsonPath("$.nit").value("710035027"))
                .andExpect(jsonPath("$.codigoSucursal").value(0))
                .andExpect(jsonPath("$.codigoPuntoVenta").value(0))
                .andExpect(jsonPath("$.cufd").value("CUFD-123"))
                .andExpect(jsonPath("$.codigoControl").value("CTRL-456"))
                .andExpect(jsonPath("$.direccion").value("LA PAZ"))
                .andExpect(jsonPath("$.fechaVigencia").value("2026-11-17T23:59:59.000-04:00"))
                .andExpect(jsonPath("$.transaccion").value(true))
                .andExpect(jsonPath("$.mensajes[0].codigo").value(926))
                .andExpect(jsonPath("$.mensajes[0].descripcion").value("COMUNICACION EXITOSA"));
    }

    @Test
    void rechazaPuntoDeVentaFueraDeRango() throws Exception {
        mockMvc.perform(post("/api/v1/empresas/1/cufd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoPuntoVenta\":2}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Solicitud inválida"))
                .andExpect(jsonPath("$.errores.codigoPuntoVenta")
                        .value("El punto de venta debe ser 0 o 1."));
    }

    @Test
    void rechazaPuntoDeVentaAusente() throws Exception {
        mockMvc.perform(post("/api/v1/empresas/1/cufd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Solicitud inválida"))
                .andExpect(jsonPath("$.errores.codigoPuntoVenta")
                        .value("El punto de venta es obligatorio."));
    }

    @Test
    void respondeEmpresaNoEncontrada() throws Exception {
        given(obtenerCufdServicio.obtener(1L, new CufdSolicitud(0)))
                .willThrow(new EmpresaNoEncontradaExcepcion(1L));

        mockMvc.perform(post("/api/v1/empresas/1/cufd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoPuntoVenta\":0}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Empresa no encontrada"))
                .andExpect(jsonPath("$.id").value(1));
    }

    @Test
    void respondeTokenNoConfigurado() throws Exception {
        given(obtenerCufdServicio.obtener(1L, new CufdSolicitud(0)))
                .willThrow(new TokenSiatNoConfiguradoExcepcion(1L));

        mockMvc.perform(post("/api/v1/empresas/1/cufd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoPuntoVenta\":0}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Token SIAT no configurado"))
                .andExpect(jsonPath("$.empresaId").value(1));
    }

    @Test
    void respondeConfiguracionSiatInvalida() throws Exception {
        given(obtenerCufdServicio.obtener(1L, new CufdSolicitud(0)))
                .willThrow(new ConfiguracionSiatInvalidaExcepcion(1L, "ambiente"));

        mockMvc.perform(post("/api/v1/empresas/1/cufd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoPuntoVenta\":0}"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Configuración SIAT inválida"))
                .andExpect(jsonPath("$.empresaId").value(1))
                .andExpect(jsonPath("$.campo").value("ambiente"));
    }

    @Test
    void respondeSiatNoDisponible() throws Exception {
        given(obtenerCufdServicio.obtener(1L, new CufdSolicitud(0)))
                .willThrow(new SiatNoDisponibleExcepcion("FacturacionCodigos", "timeout"));

        mockMvc.perform(post("/api/v1/empresas/1/cufd")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"codigoPuntoVenta\":0}"))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.title").value("Servicio SIAT no disponible"))
                .andExpect(jsonPath("$.servicio").value("FacturacionCodigos"));
    }
}
