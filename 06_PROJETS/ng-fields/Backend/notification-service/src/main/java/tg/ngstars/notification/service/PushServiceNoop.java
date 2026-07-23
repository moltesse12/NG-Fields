package tg.ngstars.notification.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import tg.ngstars.notification.dto.PushNotificationRequest;
import tg.ngstars.notification.dto.PushTokenRequest;

@Service
@ConditionalOnProperty(name = "firebase.enabled", havingValue = "false", matchIfMissing = true)
public class PushServiceNoop implements PushServiceInterface {

    private static final Logger log = LoggerFactory.getLogger(PushServiceNoop.class);

    @Override
    public void registerToken(PushTokenRequest request) {
        log.info("Push token enregistre (mode noop, firebase desactive)");
    }

    @Override
    public void sendPush(PushNotificationRequest request) {
        log.warn("Push desactive (firebase.enabled=false). Notification non envoyee a userId={}", request.userId());
    }
}
