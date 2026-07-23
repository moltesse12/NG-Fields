package tg.ngstars.notification.dto;

import java.util.Map;

import jakarta.validation.constraints.NotBlank;

public record PushNotificationRequest(
    @NotBlank String userId,
    @NotBlank String title,
    @NotBlank String body,
    Map<String, String> data
) {}
