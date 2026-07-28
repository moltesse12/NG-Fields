package tg.ngstars.auth.controller;

import java.util.Map;
import java.util.UUID;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import io.micrometer.core.annotation.Timed;
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
@Tag(name = "Users", description = "User management, profiles and authentication")
@Timed
public class UserController {

    private static final Logger log = LoggerFactory.getLogger(UserController.class);
    private static final java.util.regex.Pattern IP_PATTERN = java.util.regex.Pattern.compile(
            "^((25[0-5]|2[0-4]\\d|[01]?\\d\\d?)\\.){3}(25[0-5]|2[0-4]\\d|[01]?\\d\\d?)$");

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
    @Operation(summary = "Create user", description = "Creates a user account in Keycloak and persists in database.")
    @ApiResponse(responseCode = "201", description = "User created")
    @ApiResponse(responseCode = "409", description = "Email already in use")
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(request, jwt.getSubject(), null));
    }

    @GetMapping("/api/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "List all users", description = "Paginated. Admin only.")
    @ApiResponse(responseCode = "200", description = "Result page")
    public ResponseEntity<Page<UserResponse>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userService.getAllUsers(PageRequest.of(page, size)));
    }

    @GetMapping("/api/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Get user", description = "Full user details by ID.")
    @ApiResponse(responseCode = "200", description = "User found")
    @ApiResponse(responseCode = "404", description = "User not found")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PutMapping("/api/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user", description = "Updates info in Keycloak and database.")
    @ApiResponse(responseCode = "200", description = "User updated")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userService.updateUser(id, request, jwt.getSubject()));
    }

    @DeleteMapping("/api/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user", description = "Disables in Keycloak and database.")
    @ApiResponse(responseCode = "204", description = "User deleted")
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        userService.deleteUser(id, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/api/admin/users/{keycloakId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Assign role", description = "Changes a user's role in Keycloak.")
    @ApiResponse(responseCode = "200", description = "Role assigned")
    public ResponseEntity<UserResponse> assignRole(
            @PathVariable UUID keycloakId,
            @Valid @RequestBody RoleAssignRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                userService.assignRole(keycloakId, request.role(), jwt.getSubject()));
    }

    @PatchMapping("/api/admin/users/{keycloakId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Enable/Disable user", description = "Changes enabled status in Keycloak.")
    @ApiResponse(responseCode = "200", description = "Status updated")
    public ResponseEntity<UserResponse> updateStatus(
            @PathVariable UUID keycloakId,
            @Valid @RequestBody UserStatusRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                userService.updateUserStatus(keycloakId, request.enabled(), jwt.getSubject()));
    }

    @PostMapping("/api/admin/users/{keycloakId}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reset password", description = "Sends a reset email via Keycloak.")
    @ApiResponse(responseCode = "200", description = "Email sent")
    public ResponseEntity<Map<String, String>> resetPassword(
            @PathVariable UUID keycloakId,
            @AuthenticationPrincipal Jwt jwt) {
        userService.sendPasswordReset(keycloakId, jwt.getSubject());
        return ResponseEntity.ok(Map.of("message", "Password reset email sent"));
    }

    @GetMapping("/api/users/me")
    @Operation(summary = "Get own profile", description = "Returns the authenticated user's profile.")
    @ApiResponse(responseCode = "200", description = "Profile returned")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userService.getProfile(UUID.fromString(jwt.getSubject())));
    }

    @PutMapping("/api/users/me")
    @Operation(summary = "Update own profile", description = "Updates personal info of the authenticated user.")
    @ApiResponse(responseCode = "200", description = "Profile updated")
    public ResponseEntity<UserResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                userService.updateProfile(UUID.fromString(jwt.getSubject()), request));
    }

    @PostMapping("/api/users/me/change-password")
    @Operation(summary = "Change own password", description = "Verifies old password then applies the new one.")
    @ApiResponse(responseCode = "200", description = "Password changed")
    @ApiResponse(responseCode = "400", description = "Current password incorrect")
    public ResponseEntity<Map<String, String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal Jwt jwt,
            HttpServletRequest httpRequest) {
        String username = jwt.getClaimAsString("preferred_username");
        if (bruteForceProtection.isLockedOut(username)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "Account temporarily locked. Try again later."));
        }

        ConsumptionProbe probe = rateLimitConfig.tryConsume("change-password:" + jwt.getSubject(), 5);
        if (!probe.isConsumed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "Too many attempts. Try again in " + probe.getNanosToWaitForRefill() / 1_000_000_000 + " seconds."));
        }

        userService.changePassword(UUID.fromString(jwt.getSubject()), request);
        return ResponseEntity.ok(Map.of("message", "Password changed successfully"));
    }

    @PostMapping("/api/public/register")
    @Operation(summary = "Register client", description = "Creates a CLIENT_USER account. Public endpoint, no auth required.")
    @ApiResponse(responseCode = "201", description = "Account created")
    @ApiResponse(responseCode = "409", description = "Email already in use")
    public ResponseEntity<Map<String, Object>> register(
            @Valid @RequestBody CreateUserRequest request,
            HttpServletRequest httpRequest) {
        ConsumptionProbe probe = rateLimitConfig.tryConsume("register:" + clientIp(httpRequest), 5);
        if (!probe.isConsumed()) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body(Map.of("message", "Too many attempts. Try again later."));
        }

        var created = userService.registerClient(request, clientIp(httpRequest));

        try {
            String verificationLink = emailVerificationService.generateVerificationLink(created.id(), created.email());
            emailService.sendVerificationEmail(created.email(), created.firstName(), verificationLink);
        } catch (Exception e) {
            log.warn("Failed to send verification email to {}: {}", created.email(), e.getMessage());
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Account created. Check your email to activate your account.",
                "user", created));
    }

    @GetMapping("/api/public/verify-email")
    @Operation(summary = "Verify email address", description = "Validates the token sent by email.")
    @ApiResponse(responseCode = "200", description = "Email verified")
    @ApiResponse(responseCode = "400", description = "Token invalid or expired")
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

        return ResponseEntity.ok(Map.of("message", "Email address verified successfully"));
    }

    private static String clientIp(HttpServletRequest request) {
        var xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank()) {
            var candidate = xff.split(",")[0].trim();
            if (IP_PATTERN.matcher(candidate).matches()) return candidate;
        }
        return request.getRemoteAddr();
    }
}
