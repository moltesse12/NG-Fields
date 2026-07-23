package tg.ngstars.notification.service;

import tg.ngstars.notification.dto.PushNotificationRequest;
import tg.ngstars.notification.dto.PushTokenRequest;

public interface PushServiceInterface {
    void registerToken(PushTokenRequest request);
    void sendPush(PushNotificationRequest request);
}
