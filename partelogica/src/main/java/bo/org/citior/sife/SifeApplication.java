package bo.org.citior.sife;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class SifeApplication {

    public static void main(String[] argumentos) {
        SpringApplication.run(SifeApplication.class, argumentos);
    }
}
