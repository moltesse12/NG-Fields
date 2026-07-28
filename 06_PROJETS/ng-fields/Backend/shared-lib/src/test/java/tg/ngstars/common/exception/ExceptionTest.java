package tg.ngstars.common.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Exceptions du shared-lib")
class ExceptionTest {

    @Nested
    @DisplayName("NotFoundException")
    class NotFound {

        @Test
        @DisplayName("Constructeur avec message")
        void constructeurMessage() {
            var ex = new NotFoundException("Ressource introuvable");
            assertEquals("Ressource introuvable", ex.getMessage());
            assertTrue(ex instanceof RuntimeException);
        }

        @Test
        @DisplayName("Constructeur avec resource et id")
        void constructeurResourceId() {
            var id = java.util.UUID.randomUUID();
            var ex = new NotFoundException("User", id);
            assertEquals("User not found with id: " + id, ex.getMessage());
        }
    }

    @Nested
    @DisplayName("ConflictException")
    class Conflict {

        @Test
        @DisplayName("Constructeur avec message")
        void constructeurMessage() {
            var ex = new ConflictException("Conflit detecte");
            assertEquals("Conflit detecte", ex.getMessage());
        }

        @Test
        @DisplayName("Constructeur avec resource, champ et valeur")
        void constructeurResourceFieldValue() {
            var ex = new ConflictException("User", "email", "test@test.com");
            assertEquals("User already exists with email: test@test.com", ex.getMessage());
        }
    }

    @Nested
    @DisplayName("ForbiddenException")
    class Forbidden {

        @Test
        @DisplayName("Constructeur avec message")
        void constructeurMessage() {
            var ex = new ForbiddenException("Acces refuse");
            assertEquals("Acces refuse", ex.getMessage());
            assertTrue(ex instanceof RuntimeException);
        }
    }

    @Nested
    @DisplayName("BusinessException")
    class Business {

        @Test
        @DisplayName("Constructeur avec code et message")
        void constructeurCodeMessage() {
            var ex = new BusinessException("BIZ_001", "Regle violee");
            assertEquals("BIZ_001", ex.getCode());
            assertEquals("Regle violee", ex.getMessage());
            assertTrue(ex instanceof RuntimeException);
        }
    }

    @Nested
    @DisplayName("MediaServiceException")
    class MediaService {

        @Test
        @DisplayName("Constructeur avec message")
        void constructeurMessage() {
            var ex = new MediaServiceException("Erreur media");
            assertEquals("Erreur media", ex.getMessage());
            assertNull(ex.getCause());
        }

        @Test
        @DisplayName("Constructeur avec message et cause")
        void constructeurMessageCause() {
            var cause = new RuntimeException("cause");
            var ex = new MediaServiceException("Erreur media", cause);
            assertEquals("Erreur media", ex.getMessage());
            assertEquals(cause, ex.getCause());
        }
    }
}
