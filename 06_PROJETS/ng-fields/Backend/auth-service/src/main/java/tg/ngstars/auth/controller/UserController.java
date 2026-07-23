package tg.ngstars.auth.controller;

import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.bucket4j.ConsumptionProbe;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import tg.ngstars.auth.config.RateLimitConfig;
import tg.ngstars.auth.dto.ChangePasswordRequest;
import tg.ngstars.auth.dto.CreateUserRequest;
import tg.ngstars.auth.dto.RoleAssignRequest;
import tg.ngstars.auth.dto.UpdateProfileRequest;
import tg.ngstars.auth.dto.UpdateUserRequest;
import tg.ngstars.auth.dto.UserResponse;
import tg.ngstars.auth.dto.UserStatusRequest;
import tg.ngstars.auth.service.BruteForceProtectionService;
import tg.ngstars.auth.service.EmailService;
import tg.ngstars.auth.service.EmailVerificationService;
import tg.ngstars.auth.service.UserService;

@RestController
@Tag(name = "Users", description = "Gestion des utilisateurs, profils et authentification")
public class UserController {

    private final UserService userService;
    private final BruteForceProtectionService bruteForceProtection;
    private final EmailVerificationService emailVerificationService;
    private final RateLimitConfig rateLimitConfig;
    private final EmailService emailService;

    public UserController(UserService userService,
            BruteForceProtectionService bruteForceProtection,
            EmailVerificationService emailVerificationService,
            RateLimitConfig rateLimitConfig,
            EmailService emailService) {
        this.userService = userService;
        this.bruteForceProtection = bruteForceProtection;
        this.emailVerificationService = emailVerificationService;
        this.rateLimitConfig = rateLimitConfig;
        this.emailService = emailService;
    }

    @PostMapping("/api/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Creer un utilisateur", description = "Cree un compte utilisateur dans Keycloak et enregistre en base.")
    @ApiResponse(responseCode = "201", description = "Utilisateur cree")
    @ApiResponse(responseCode = "409", description = "Email deja utilise")
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(request, jwt.getSubject(), null));
    }

    @GetMapping("/api/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Lister tous les utilisateurs", description = "Pagine. Admin uniquement.")
    @ApiResponse(responseCode = "200", description = "Page de resultats")
    public ResponseEntity<Page<UserResponse>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userService.getAllUsers(PageRequest.of(page, size)));
    }

    @GetMapping("/api/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Obtenir un utilisateur", description = "Detail complet d'un utilisateur par son ID.")
    @ApiResponse(responseCode = "200", description = "Utilisateur trouve")
    @ApiResponse(responseCode = "404", description = "Utilisateur introuvable")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PutMapping("/api/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mettre a jour un utilisateur", description = "Met a jour les infos dans Keycloak et en base.")
    @ApiResponse(responseCode = "200", description = "Utilisateur mis a jour")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userService.updateUser(id, request, jwt.getSubject()));
    }

    @DeleteMapping("/api/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Supprimer un utilisateur", description = "Desactive dans Keycloak et en base.")
    @ApiResponse(responseCode = "204", description = "Utilisateur supprime")
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        userService.deleteUser(id, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/api/admin/users/{keycloakId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assigner un role", description = "Change le role d'un utilisateur dans Keycloak.")
    @ApiResponse(responseCode = "200", description = "Role assigne")
    public ResponseEntity<UserResponse> assignRole(
            @PathVariable UUID keycloakId,
            @Valid @RequestBody RoleAssignRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                userService.assignRole(keycloakId, request.role(), jwt.getSubject()));
    }

    @PatchMapping("/api/admin/users/{keycloakId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Activer/Desactiver un utilisateur", description = "Change le statut enabled dans Keycloak.")
    @ApiResponse(responseCode = "200", description = "Statut mis a jour")
    public ResponseEntity<UserResponse> updateStatus(
            @PathVariable UUID keycloakId,
            @RequestBody UserStatusRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                userService.updateUserStatus(keycloakId, request.enabled(), jwt.getSubject()));
    }

    @PostMapping("/api/admin/users/{keycloakId}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reinitialiser le mot de passe", description = "Envoie un email de reinitialisation via Keycloak.")
    @ApiResponse(responseCode = "200", description = "Email envoye")
    public ResponseEntity<Map<String, String>> resetPassword(
            @PathVariable UUID keycloakId,
            @AuthenticationPrincipal Jwt jwt) {
        userService.sendPasswordReset(keycloakId, jwt.getSubject());
        return ResponseEntity.ok(Map.of("message", "Email de reinitialisation envoye"));
    }

    @GetMapping("/api/users/me")
    @Operation(summary = "Obtenir son profil", description = "Retourne le profil de l'utilisateur connecte.")
    @ApiResponse(responseCode = "200", description = "Profil retourne")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userService.getProfile(UUID.fromString(jwt.getSubject())));
    }

    @PutMapping("/api/users/me")
    @Operation(summary = "Mettre a jour son profil", description = "Met a jour les infos personnelles de l'utilisateur connecte.")
    @ApiResponse(responseCode = "200", description = "Profil mis a jour")
    public ResponseEntity<UserResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                userService.updateProfile(UUID.fromString(jwt.getSubject()), request));
    }

    @PostMapping("/api/users/me/change-password")
    @Operation(summary = "Changer son mot de passe", description = "Verifie l'ancien mot de passe puis applique le nouveau.")
    @ApiResponse(responseCode = "200", description = "Mot de passe modifie")
    @ApiResponse(responseCode = "400", description = "Ancien mot de passe incorrect")
    public ResponseEntity<Map<String, String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest httpRequest) {
        String username = jwt.getClaimAsString("preferred_username");
        if (bruteForceProtection.isLockedOut(username)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "Compte temporairement bloque. Reessayez plus tard."));
        }

        ConsumptionProbe probe = rateLimitConfig.tryConsume("change-password:" + jwt.getSubject(), 5);
        if (!probe.isConsumed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "Trop de tentatives. Reessayez dans " + probe.getNanosToWaitForRefill() / 1_000_000_000 + " secondes."));
        }

        userService.changePassword(UUID.fromString(jwt.getSubject()), request);
        return ResponseEntity.ok(Map.of("message", "Mot de passe modifie avec succes"));
    }

    @PostMapping("/api/public/register")
    @Operation(summary = "Inscription client", description = "Cree un compte CLIENT_USER. Endpoint public, pas d'auth requise.")
    @ApiResponse(responseCode = "201", description = "Compte cree")
    @ApiResponse(responseCode = "409", description = "Email deja utilise")
    public ResponseEntity<Map<String, Object>> register(
            @Valid @RequestBody CreateUserRequest request,
            HttpServletRequest httpRequest) {
        ConsumptionProbe probe = rateLimitConfig.tryConsume("register:" + clientIp(httpRequest), 5);
        if (!probe.isConsumed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "Trop de tentatives. Reessayez plus tard."));
        }

        var created = userService.registerClient(request, clientIp(httpRequest));

        try {
            String verificationLink = emailVerificationService.generateVerificationLink(created.id(), created.email());
            emailService.sendVerificationEmail(created.email(), created.firstName(), verificationLink);
        } catch (Exception e) {
            // Email verification is best-effort
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Compte cree. Verifiez votre email pour activer votre compte.",
                "user", created));
    }

    @GetMapping("/api/public/verify-email")
    @Operation(summary = "Verifier l'adresse email", description = "Valide le token de verification envoye par email.")
    @ApiResponse(responseCode = "200", description = "Email verifie")
    @ApiResponse(responseCode = "400", description = "Token invalide ou expire")
    public ResponseEntity<Map<String, String>> verifyEmail(@RequestParam String token) {
        var result = emailVerificationService.verifyToken(token);

        if (result.expired()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", result.errorMessage(), "error", "expired"));
        }

        if (!result.valid()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", result.errorMessage(), "error", "invalid"));
        }

        userService.markEmailVerified(result.userId());

        return ResponseEntity.ok(Map.of("message", "Adresse email verifiee avec succes"));
    }

    private static String clientIp(HttpServletRequest request) {
        var xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank())
            return xff.split(",")[0].trim();
        return request.getRemoteAddr();
    }
}
