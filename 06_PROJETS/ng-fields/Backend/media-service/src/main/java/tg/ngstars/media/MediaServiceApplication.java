package tg.ngstars.media;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import tg.ngstars.media.config.MediaProperties;

@SpringBootApplication
@EnableConfigurationProperties(MediaProperties.class)
public class MediaServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(MediaServiceApplication.class, args);
    }
}
