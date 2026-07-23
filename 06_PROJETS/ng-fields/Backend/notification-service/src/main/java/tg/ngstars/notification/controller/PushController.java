package tg.ngstars.notification.controller;

import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import tg.ngstars.notification.dto.PushNotificationRequest;
import tg.ngstars.notification.dto.PushTokenRequest;
import tg.ngstars.notification.service.PushServiceInterface;
import tg.ngstars.notification.service.RateLimiter;

@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
@Tag(name = "Notifications", description = "Push notifications et gestion des tokens")
public class PushController {

    private static final int MAX_PUSH_PER_MINUTE = 20;

    private final PushServiceInterface pushService;
    private final RateLimiter rateLimiter;

    public PushController(PushServiceInterface pushService, RateLimiter rateLimiter) {
        this.pushService = pushService;
        this.rateLimiter = rateLimiter;
    }

    private String currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt)
            return jwt.getSubject();
        return "anonymous";
    }

    @PostMapping("/push/token")
    @Operation(summary = "Enregistrer un push token FCM", description = "Associe un token Firebase Cloud Messaging a l'utilisateur connecte.")
    @ApiResponse(responseCode = "200", description = "Token enregistre")
    @ApiResponse(responseCode = "400", description = "Token invalide")
    public ResponseEntity<Map<String, String>> registerPushToken(
            @Valid @RequestBody PushTokenRequest request) {
        pushService.registerToken(request);
        return ResponseEntity.ok(Map.of("message", "Push token enregistre"));
    }

    @PostMapping("/push/send")
    @Operation(summary = "Envoyer une notification push", description = "Envoie une notification push via Firebase Cloud Messaging. Rate limit: 20 push/min par user.")
    @ApiResponse(responseCode = "202", description = "Notification envoyee en file")
    @ApiResponse(responseCode = "429", description = "Rate limit depasse")
    @ApiResponse(responseCode = "503", description = "Firebase non configure")
    public ResponseEntity<?> sendPush(
            @Valid @RequestBody PushNotificationRequest request) {
        var userId = currentUserId();
        if (!rateLimiter.tryAcquire("push:" + userId, MAX_PUSH_PER_MINUTE)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("error", "Rate limit depasse. Max " + MAX_PUSH_PER_MINUTE + " push/min."));
        }
        pushService.sendPush(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED)
                .body(Map.of("message", "Notification push envoyee"));
    }
}
