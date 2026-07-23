package tg.ngstars.interv;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class InterventionServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(InterventionServiceApplication.class, args);
    }
}
