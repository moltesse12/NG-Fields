package tg.ngstars.gateway.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("FallbackResponse")
class FallbackResponseTest {

    @Nested
    @DisplayName("of(routeId, detail)")
    class Of {

        @Test
        @DisplayName("Cree une reponse 503 avec les champs obligatoires")
        void creeReponse503() {
            var response = FallbackResponse.of("auth-service", "Service indisponible");

            assertEquals("about:blank", response.type());
            assertEquals("Service Unavailable", response.title());
            assertEquals(503, response.status());
            assertEquals("Service indisponible", response.detail());
            assertEquals("auth-service", response.routeId());
            assertNotNull(response.timestamp());
        }

        @Test
        @DisplayName("Le timestamp est proche de l'instant actuel")
        void timestampProcheMaintenant() {
            var avant = Instant.now();
            var response = FallbackResponse.of("route", "detail");
            var apres = Instant.now();

            assertFalse(response.timestamp().isBefore(avant));
            assertFalse(response.timestamp().isAfter(apres));
        }
    }

    @Nested
    @DisplayName("Constructeur direct")
    class ConstructeurDirect {

        @Test
        @DisplayName("Cree un record avec tous les champs")
        void creeRecord() {
            var ts = Instant.now();
            var response = new FallbackResponse("type", "title", 503, "detail", "route", ts);

            assertEquals("type", response.type());
            assertEquals("title", response.title());
            assertEquals(503, response.status());
            assertEquals("detail", response.detail());
            assertEquals("route", response.routeId());
            assertEquals(ts, response.timestamp());
        }
    }
}
