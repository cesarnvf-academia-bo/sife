package bo.org.citior.sife.configuracion;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracionOpenApi {

    @Bean
    public OpenAPI documentacionSife() {
        return new OpenAPI().info(new Info()
                .title("API SIFE")
                .description("Documentación de la parte lógica del sistema SIFE")
                .version("1.0.0"));
    }
}
