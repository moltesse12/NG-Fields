package tg.ngstars.client.service;

import java.net.URI;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class InterventionSyncClient {

    private static final Logger log = LoggerFactory.getLogger(InterventionSyncClient.class);

    private final RestClient restClient;

    public InterventionSyncClient(
            @Value("${intervention-service.url:http://localhost:8083}") String interventionServiceUrl) {
        this.restClient = RestClient.builder()
                .baseUrl(interventionServiceUrl)
                .build();
    }

    public void syncClientData(UUID clientId, String name, String email, String phone, String address) {
        try {
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
                log.info("Sync client data vers intervention-service: {} interventions mises à jour", updated);
            }
        } catch (Exception e) {
            log.warn("Échec sync client data vers intervention-service: {}", e.getMessage(), e);
        }
    }
}
