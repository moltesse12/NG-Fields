package tg.ngstars.gateway.health;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.health.contributor.Health;
import org.springframework.boot.health.contributor.HealthIndicator;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
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

        Map<String, String> results = new LinkedHashMap<>();
        boolean allUp = true;

        for (Map.Entry<String, String> entry : services.entrySet()) {
            String name = entry.getKey();
            String url = entry.getValue();
            try {
                String status = webClient.get()
                    .uri(url + "/actuator/health")
                    .retrieve()
                    .bodyToMono(String.class)
                    .timeout(Duration.ofSeconds(2))
                    .onErrorResume(e -> Mono.just("DOWN"))
                    .block();

                if ("DOWN".equals(status) || status == null) {
                    results.put(name, "DOWN");
                    allUp = false;
                } else {
                    results.put(name, "UP");
                }
            } catch (Exception e) {
                results.put(name, "DOWN");
                allUp = false;
                log.debug("Health check failed for {}: {}", name, e.getMessage());
            }
        }

        Health.Builder builder = allUp ? Health.up() : Health.down();
        results.forEach(builder::withDetail);
        return builder.build();
    }
}
