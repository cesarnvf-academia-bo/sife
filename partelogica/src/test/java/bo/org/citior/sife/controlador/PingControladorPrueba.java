package bo.org.citior.sife.controlador;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import bo.org.citior.sife.dominio.RespuestaPing;
import bo.org.citior.sife.servicio.PingServicio;
import java.time.Instant;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(PingControlador.class)
class PingControladorPrueba {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PingServicio pingServicio;

    @Test
    void responderPing() throws Exception {
        RespuestaPing respuesta = new RespuestaPing("activo", "pong", Instant.parse("2026-01-01T00:00:00Z"));
        given(pingServicio.obtenerRespuesta()).willReturn(respuesta);

        mockMvc.perform(get("/api/v1/ping"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(jsonPath("$.estado").value("activo"))
                .andExpect(jsonPath("$.mensaje").value("pong"))
                .andExpect(jsonPath("$.marcaDeTiempo").value("2026-01-01T00:00:00Z"));
    }
}
