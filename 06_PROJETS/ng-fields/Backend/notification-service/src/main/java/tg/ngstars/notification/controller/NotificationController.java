package tg.ngstars.notification.controller;

import jakarta.validation.Valid;

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
import tg.ngstars.notification.dto.EmailRequest;
import tg.ngstars.notification.service.EmailService;
import tg.ngstars.notification.service.RateLimiter;

@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
@Tag(name = "Email Notifications", description = "Envoi d'emails via templates Thymeleaf")
public class NotificationController {

    private static final int MAX_EMAILS_PER_MINUTE = 10;

    private final EmailService emailService;
    private final RateLimiter rateLimiter;

    public NotificationController(EmailService emailService, RateLimiter rateLimiter) {
        this.emailService = emailService;
        this.rateLimiter = rateLimiter;
    }

    private String currentUserId() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof Jwt jwt)
            return jwt.getSubject();
        return "anonymous";
    }

    @PostMapping("/email")
    @Operation(summary = "Envoyer un email", description = "Envoie un email via template Thymeleaf. Rate limit: 10 emails/min par user.")
    @ApiResponse(responseCode = "202", description = "Email envoye")
    @ApiResponse(responseCode = "429", description = "Rate limit depasse")
    @ApiResponse(responseCode = "400", description = "Template non autorise")
    public ResponseEntity<?> sendEmail(@Valid @RequestBody EmailRequest request) {
        var userId = currentUserId();
        if (!rateLimiter.tryAcquire("email:" + userId, MAX_EMAILS_PER_MINUTE)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(java.util.Map.of("error", "Rate limit depasse. Max " + MAX_EMAILS_PER_MINUTE + " emails/min."));
        }
        emailService.send(request);
        return ResponseEntity.status(HttpStatus.ACCEPTED).build();
    }
}
