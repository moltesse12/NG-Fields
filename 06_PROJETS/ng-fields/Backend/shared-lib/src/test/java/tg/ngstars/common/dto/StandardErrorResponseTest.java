package tg.ngstars.common.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("StandardErrorResponse")
class StandardErrorResponseTest {

    @Nested
    @DisplayName("of(code, message, path)")
    class OfSansDetails {

        @Test
        @DisplayName("Cree une reponse avec les champs obligatoires")
        void creeReponseAvecChampsObligatoires() {
            var response = StandardErrorResponse.of("E001", "Erreur test", "/api/test");

            assertEquals("E001", response.code());
            assertEquals("Erreur test", response.message());
            assertNotNull(response.timestamp());
            assertEquals("/api/test", response.path());
            assertNull(response.details());
        }

        @Test
        @DisplayName("Le timestamp est proche de l'instant actuel")
        void timestampProcheMaintenant() {
            var avant = Instant.now();
            var response = StandardErrorResponse.of("E001", "msg", "/path");
            var apres = Instant.now();

            assertFalse(response.timestamp().isBefore(avant));
            assertFalse(response.timestamp().isAfter(apres));
        }
    }

    @Nested
    @DisplayName("of(code, message, path, details)")
    class OfAvecDetails {

        @Test
        @DisplayName("Cree une reponse avec details")
        void creeReponseAvecDetails() {
            var details = Map.of("field", "email", "error", " invalide");
            var response = StandardErrorResponse.of("E002", "Erreur validation", "/api/users", details);

            assertEquals("E002", response.code());
            assertEquals("Erreur validation", response.message());
            assertEquals("/api/users", response.path());
            assertNotNull(response.details());
            assertEquals("email", response.details().get("field"));
            assertEquals(" invalide", response.details().get("error"));
        }

        @Test
        @DisplayName("Details vide est accepte")
        void detailsVide() {
            var response = StandardErrorResponse.of("E003", "msg", "/path", Map.of());

            assertNotNull(response.details());
            assertTrue(response.details().isEmpty());
        }
    }
}
