package tg.ngstars.client.service;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;

@Component
public class InterventionSyncClient {

    private static final Logger log = LoggerFactory.getLogger(InterventionSyncClient.class);

    private final RestClient restClient;

    public InterventionSyncClient(
            @Value("${intervention-service.url:http://localhost:8083}") String interventionServiceUrl) {

        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(3));
        requestFactory.setReadTimeout(Duration.ofSeconds(5));

        this.restClient = RestClient.builder()
                .baseUrl(interventionServiceUrl)
                .requestFactory(requestFactory)
                .build();
    }

    @CircuitBreaker(name = "interventionSync", fallbackMethod = "syncFallback")
    @Retry(name = "interventionSync")
    public void syncClientData(UUID clientId, String name, String email, String phone, String address) {
        Map<String, Object> body = Map.of(
                "clientId", clientId.toString(),
                "clientName", name != null ? name : "",
                "clientEmail", email != null ? email : "",
                "clientPhone", phone != null ? phone : "",
                "clientAddress", address != null ? address : "");

        ResponseEntity<Map> response = restClient.post()
                .uri("/api/interventions/sync/client-data")
                .contentType(MediaType.APPLICATION_JSON)
                .body(body)
                .retrieve()
                .toEntity(Map.class);

        if (response.getBody() != null) {
            Object updated = response.getBody().get("updated");
            log.info("Sync client data vers intervention-service: {} interventions mises a jour", updated);
        }
    }

    private void syncFallback(UUID clientId, String name, String email, String phone, String address, Throwable t) {
        log.warn("Fallback sync client data pour clientId={}: {}", clientId, t.getMessage());
    }
}
