package tg.ngstars.notification.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tg.ngstars.notification.dto.PushNotificationRequest;
import tg.ngstars.notification.dto.PushTokenRequest;

import java.util.Map;

@DisplayName("PushServiceNoop")
class PushServiceNoopTest {

    private PushServiceNoop pushService;

    @BeforeEach
    void setUp() {
        pushService = new PushServiceNoop();
    }

    @Nested
    @DisplayName("registerToken()")
    class RegisterToken {

        @Test
        @DisplayName("Accepte le token sans erreur")
        void registerToken_noException() {
            var request = new PushTokenRequest("fcm-token-abc123", "android");
            assertDoesNotThrow(() -> pushService.registerToken(request));
        }
    }

    @Nested
    @DisplayName("sendPush()")
    class SendPush {

        @Test
        @DisplayName("Ne lève pas d'erreur en mode noop")
        void sendPush_noException() {
            var request = new PushNotificationRequest(
                    "user-123", "Test Title", "Test Body", Map.of("type", "TEST"));
            assertDoesNotThrow(() -> pushService.sendPush(request));
        }

        @Test
        @DisplayName("Accepte les données nulles")
        void sendPush_nullData_noException() {
            var request = new PushNotificationRequest(
                    "user-456", "Title", "Body", null);
            assertDoesNotThrow(() -> pushService.sendPush(request));
        }
    }
}
