package bo.org.citior.sife.configuracion;

import org.apache.cxf.bus.spring.SpringBus;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(PropiedadesSiat.class)
public class ConfiguracionCxf {

    @Bean
    public SpringBus busCxf() {
        return new SpringBus();
    }
}
