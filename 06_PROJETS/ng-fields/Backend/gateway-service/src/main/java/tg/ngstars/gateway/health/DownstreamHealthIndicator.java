package tg.ngstars.gateway.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class DownstreamHealthIndicator implements HealthIndicator {

    private static final Logger log = LoggerFactory.getLogger(DownstreamHealthIndicator.class);

    private final WebClient webClient;

    @Value("${AUTH_SERVICE_URL:http://localhost:8081}")
    private String authServiceUrl;

    @Value("${CLIENT_SERVICE_URL:http://localhost:8082}")
    private String clientServiceUrl;

    @Value("${INTERVENTION_SERVICE_URL:http://localhost:8083}")
    private String interventionServiceUrl;

    @Value("${MEDIA_SERVICE_URL:http://localhost:8084}")
    private String mediaServiceUrl;

    @Value("${NOTIFICATION_SERVICE_URL:http://localhost:8085}")
    private String notificationServiceUrl;

    @Value("${REPORT_SERVICE_URL:http://localhost:8086}")
    private String reportServiceUrl;

    public DownstreamHealthIndicator(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public Health health() {
        Map<String, String> services = new LinkedHashMap<>();
        services.put("auth-service", authServiceUrl);
        services.put("client-service", clientServiceUrl);
        services.put("intervention-service", interventionServiceUrl);
        services.put("media-service", mediaServiceUrl);
        services.put("notification-service", notificationServiceUrl);
        services.put("report-service", reportServiceUrl);

        Map<String, String> results = new java.util.concurrent.ConcurrentHashMap<>();

        try {
            Flux.fromIterable(services.entrySet())
                .flatMap(entry -> webClient.get()
                    .uri(entry.getValue() + "/actuator/health")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(2))
                    .map(status -> Map.entry(entry.getKey(), "UP"))
                    .onErrorResume(e -> Mono.just(Map.entry(entry.getKey(), "DOWN"))), 6)
                .doOnNext(entry -> results.put(entry.getKey(), entry.getValue()))
                .blockLast(Duration.ofSeconds(5));
        } catch (Exception e) {
            log.warn("Downstream health check timed out: {}", e.getMessage());
        }

        Health.Builder builder = Health.up();
        results.forEach(builder::withDetail);
        return builder.build();
    }
}
