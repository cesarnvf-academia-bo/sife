package bo.org.citior.sife.controlador;

import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bo.org.citior.sife.dominio.dto.RespuestaVerificacionComunicacion;
import bo.org.citior.sife.dominio.dto.ResultadoServicioSiat;
import bo.org.citior.sife.excepcion.EmpresaNoEncontradaExcepcion;
import bo.org.citior.sife.excepcion.TokenSiatNoConfiguradoExcepcion;
import bo.org.citior.sife.servicio.VerificarComunicacionServicio;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(VerificarComunicacionControlador.class)
class VerificarComunicacionControladorPrueba {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VerificarComunicacionServicio verificarComunicacionServicio;

    @BeforeEach
    void reiniciarSimulacion() {
        reset(verificarComunicacionServicio);
    }

    @Test
    void respondeVerificacion() throws Exception {
        RespuestaVerificacionComunicacion respuesta = new RespuestaVerificacionComunicacion(
                1L,
                "123456789",
                Instant.parse("2026-11-17T02:00:00Z"),
                List.of(
                        new ResultadoServicioSiat(
                                "FacturacionCodigos", true, 0, "Comunicación exitosa"),
                        new ResultadoServicioSiat(
                                "FacturacionSincronizacion", true, 0, "Comunicación exitosa"),
                        new ResultadoServicioSiat(
                                "FacturacionOperaciones", true, 0, "Comunicación exitosa"),
                        new ResultadoServicioSiat(
                                "FacturacionCompraventa", true, 0, "Comunicación exitosa")));
        given(verificarComunicacionServicio.verificar(1L)).willReturn(respuesta);

        mockMvc.perform(get("/api/v1/empresas/1/verificar-comunicacion"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.empresaId").value(1))
                .andExpect(jsonPath("$.nit").value("123456789"))
                .andExpect(jsonPath("$.fechaVerificacion").value("2026-11-17T02:00:00Z"))
                .andExpect(jsonPath("$.servicios.length()").value(4))
                .andExpect(jsonPath("$.servicios[0].servicio").value("FacturacionCodigos"))
                .andExpect(jsonPath("$.servicios[0].comunicacion").value(true))
                .andExpect(jsonPath("$.servicios[0].codigo").value(0))
                .andExpect(jsonPath("$.servicios[3].servicio").value("FacturacionCompraventa"));
    }

    @Test
    void respondeTokenNoConfigurado() throws Exception {
        given(verificarComunicacionServicio.verificar(1L))
                .willThrow(new TokenSiatNoConfiguradoExcepcion(1L));

        mockMvc.perform(get("/api/v1/empresas/1/verificar-comunicacion"))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.title").value("Token SIAT no configurado"))
                .andExpect(jsonPath("$.detail")
                        .value("La empresa 1 no tiene token SIAT configurado."))
                .andExpect(jsonPath("$.empresaId").value(1));
    }

    @Test
    void respondeEmpresaNoEncontrada() throws Exception {
        given(verificarComunicacionServicio.verificar(1L))
                .willThrow(new EmpresaNoEncontradaExcepcion(1L));

        mockMvc.perform(get("/api/v1/empresas/1/verificar-comunicacion"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Empresa no encontrada"))
                .andExpect(jsonPath("$.detail")
                        .value("No se encontró la empresa con identificador 1."))
                .andExpect(jsonPath("$.id").value(1));
    }
}
