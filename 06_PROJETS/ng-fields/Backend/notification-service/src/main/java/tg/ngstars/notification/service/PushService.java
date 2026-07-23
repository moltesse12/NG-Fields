package tg.ngstars.notification.service;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;

import jakarta.annotation.PostConstruct;
import tg.ngstars.notification.dto.PushNotificationRequest;
import tg.ngstars.notification.dto.PushTokenRequest;

@Service
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "true")
public class PushService implements PushServiceInterface {

    private static final Logger log = LoggerFactory.getLogger(PushService.class);

    private final Map<String, String> tokenStore = new ConcurrentHashMap<>();

    @Value("${firebase.service-account-path:}")
    private String serviceAccountPath;

    private boolean firebaseInitialized = false;

    @PostConstruct
    public void init() {
        if (serviceAccountPath == null || serviceAccountPath.isBlank()) {
            log.warn("Firebase service account non configure (FIREBASE_SERVICE_ACCOUNT). Push desactive.");
            return;
        }
        try {
            var serviceAccount = new FileInputStream(serviceAccountPath);
            var options = FirebaseOptions.builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .build();
            FirebaseApp.initializeApp(options);
            firebaseInitialized = true;
        } catch (IOException e) {
            log.error("Erreur initialisation Firebase: {}", e.getMessage());
        }
    }

    @Override
    public void registerToken(PushTokenRequest request) {
        log.info("Push token enregistre (platform={})", request.platform());
        tokenStore.put("current-user", request.token());
    }

    @Override
    public void sendPush(PushNotificationRequest request) {
        var token = tokenStore.get(request.userId());
        if (token == null) {
            log.warn("Aucun push token pour userId={}", request.userId());
            return;
        }

        if (!firebaseInitialized) {
            log.warn("Firebase non initialise. Push non envoye a userId={}. " +
                     "Configurez FIREBASE_SERVICE_ACCOUNT pour activer.", request.userId());
            return;
        }

        try {
            var message = Message.builder()
                    .setToken(token)
                    .setNotification(Notification.builder()
                            .setTitle(request.title())
                            .setBody(request.body())
                            .build())
                    .putAllData(request.data() != null ? request.data() : Map.of())
                    .build();

            var response = FirebaseMessaging.getInstance().send(message);
            log.info("Push envoye a userId={} messageId={}", request.userId(), response);
        } catch (FirebaseMessagingException e) {
            log.error("Erreur envoi push a userId={}: {}", request.userId(), e.getMessage());
            tokenStore.remove(request.userId());
        }
    }
}
