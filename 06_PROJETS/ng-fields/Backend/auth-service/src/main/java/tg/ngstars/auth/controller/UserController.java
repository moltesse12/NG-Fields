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

import tg.ngstars.auth.dto.ChangePasswordRequest;
import tg.ngstars.auth.dto.CreateUserRequest;
import tg.ngstars.auth.dto.RoleAssignRequest;
import tg.ngstars.auth.dto.UpdateProfileRequest;
import tg.ngstars.auth.dto.UpdateUserRequest;
import tg.ngstars.auth.dto.UserResponse;
import tg.ngstars.auth.dto.UserStatusRequest;
import tg.ngstars.auth.service.UserService;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/api/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> createUser(
            @Valid @RequestBody CreateUserRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.createUser(request, jwt.getSubject(), null));
    }

    @GetMapping("/api/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<UserResponse>> getUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(userService.getAllUsers(PageRequest.of(page, size)));
    }

    @GetMapping("/api/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID id) {
        return ResponseEntity.ok(userService.getUser(id));
    }

    @PutMapping("/api/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateUser(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateUserRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userService.updateUser(id, request, jwt.getSubject()));
    }

    @DeleteMapping("/api/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        userService.deleteUser(id, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/api/admin/users/{keycloakId}/roles")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> assignRole(
            @PathVariable UUID keycloakId,
            @Valid @RequestBody RoleAssignRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                userService.assignRole(keycloakId, request.role(), jwt.getSubject()));
    }

    @PatchMapping("/api/admin/users/{keycloakId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserResponse> updateStatus(
            @PathVariable UUID keycloakId,
            @RequestBody UserStatusRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                userService.updateUserStatus(keycloakId, request.enabled(), jwt.getSubject()));
    }

    @PostMapping("/api/admin/users/{keycloakId}/reset-password")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Map<String, String>> resetPassword(
            @PathVariable UUID keycloakId,
            @AuthenticationPrincipal Jwt jwt) {
        userService.sendPasswordReset(keycloakId, jwt.getSubject());
        return ResponseEntity.ok(Map.of("message", "Email de reinitialisation envoye"));
    }

    @GetMapping("/api/users/me")
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(userService.getProfile(UUID.fromString(jwt.getSubject())));
    }

    @PutMapping("/api/users/me")
    public ResponseEntity<UserResponse> updateProfile(
            @Valid @RequestBody UpdateProfileRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(
                userService.updateProfile(UUID.fromString(jwt.getSubject()), request));
    }

    @PostMapping("/api/users/me/change-password")
    public ResponseEntity<Map<String, String>> changePassword(
            @Valid @RequestBody ChangePasswordRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        userService.changePassword(UUID.fromString(jwt.getSubject()), request);
        return ResponseEntity.ok(Map.of("message", "Mot de passe modifie avec succes"));
    }

    @PostMapping("/api/public/register")
    public ResponseEntity<Map<String, Object>> register(
            @Valid @RequestBody CreateUserRequest request,
            HttpServletRequest httpRequest) {
        var created = userService.registerClient(request, clientIp(httpRequest));
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of(
                "message", "Compte cree. Vous pouvez vous connecter sur le portail client.",
                "user", created));
    }

    private static String clientIp(HttpServletRequest request) {
        var xff = request.getHeader("X-Forwarded-For");
        if (xff != null && !xff.isBlank())
            return xff.split(",")[0].trim();
        return request.getRemoteAddr();
    }
}
