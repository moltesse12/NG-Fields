package tg.ngstars.auth.service;

import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import jakarta.ws.rs.core.Response;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tg.ngstars.auth.config.KeycloakProperties;
import tg.ngstars.auth.dto.CreateUserRequest;
import tg.ngstars.auth.dto.ChangePasswordRequest;
import tg.ngstars.auth.dto.UpdateProfileRequest;
import tg.ngstars.auth.dto.UpdateUserRequest;
import tg.ngstars.auth.dto.UserResponse;
import tg.ngstars.common.exception.ConflictException;
import tg.ngstars.common.exception.NotFoundException;
import tg.ngstars.auth.model.User;
import tg.ngstars.auth.repository.UserRepository;

@Service
public class UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);
    private static final List<String> METIER_ROLES = List.of("ADMIN", "MANAGER", "TECHNICIAN", "CLIENT_ADMIN", "CLIENT_USER", "CLIENT_VIEWER");

    private final UserRepository userRepository;
    private final AuditService auditService;
    private final EmailService emailService;
    private final Keycloak keycloak;
    private final KeycloakProperties keycloakProperties;
    private final HttpClient httpClient;

    public UserService(UserRepository userRepository, AuditService auditService,
            EmailService emailService,
            Keycloak keycloak, KeycloakProperties keycloakProperties) {
        this.userRepository = userRepository;
        this.auditService = auditService;
        this.emailService = emailService;
        this.keycloak = keycloak;
        this.keycloakProperties = keycloakProperties;
        this.httpClient = HttpClient.newHttpClient();
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public UserResponse createUser(CreateUserRequest request, String createdBy, String ip) {
        UUID createdById = userIdOrNull(createdBy);
        if (userRepository.existsByUsername(request.username()))
            throw new ConflictException("Username '" + request.username() + "' already exists");
        if (userRepository.existsByEmail(request.email()))
            throw new ConflictException("Email '" + request.email() + "' already exists");

        var kcUser = new UserRepresentation();
        kcUser.setUsername(request.username());
        kcUser.setEmail(request.email());
        kcUser.setFirstName(request.firstName());
        kcUser.setLastName(request.lastName());
        kcUser.setEnabled(true);
        if (request.password() != null)
            kcUser.setCredentials(List.of(passwordCredential(request.password())));

        var realm = realm();
        UUID keycloakId = null;
        try (Response response = realm.users().create(kcUser)) {
            if (response.getStatus() == 409) {
                // Ponytail: user already exists in Keycloak (race condition), try to get existing
                var existing = realm.users().search(request.username()).stream().findFirst();
                if (existing.isPresent()) {
                    keycloakId = UUID.fromString(existing.get().getId());
                    log.warn("Keycloak user {} already exists, reusing keycloakId={}", request.username(), keycloakId);
                } else {
                    throw new ConflictException("Keycloak user already exists: " + request.username());
                }
            } else if (response.getStatus() != 201) {
                var errorBody = response.readEntity(String.class);
                log.error("Keycloak user creation failed: status={}, body={}", response.getStatus(), errorBody);
                throw new RuntimeException("Account creation failed: " + response.getStatus());
            }

            if (keycloakId == null) {
                var location = response.getLocation();
                keycloakId = UUID.fromString(location.getPath().substring(location.getPath().lastIndexOf('/') + 1));
            }

            assignRealmRole(keycloakId.toString(), request.role());

            var user = new User();
            user.setKeycloakId(keycloakId);
            user.setUsername(request.username());
            user.setEmail(request.email());
            user.setFirstName(request.firstName());
            user.setLastName(request.lastName());
            user.setRole(request.role());
            user.setPhone(request.phone());
            user.setActive(true);
            user.setEmailVerified(false);
            userRepository.save(user);

            auditService.log(createdById, "USER_CREATED", "User", user.getId().toString(),
                    "Account created: " + request.username(), ip);
            log.info("Account created: {} (keycloakId={})", request.username(), keycloakId);

            return toResponse(user);
        } catch (RuntimeException e) {
            if (keycloakId != null) {
                try {
                    realm.users().get(keycloakId.toString()).remove();
                    log.warn("Keycloak user {} cleaned up after DB failure", keycloakId);
                } catch (Exception ignored) {
                    log.warn("Failed to cleanup Keycloak user {}", keycloakId, ignored);
                }
            }
            throw e;
        }
    }

    @Cacheable(value = "users", key = "'all_' + #pageable.pageNumber")
    @Transactional(readOnly = true)
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable).map(this::toResponse);
    }

    @Cacheable(value = "users", key = "#id")
    @Transactional(readOnly = true)
    public UserResponse getUser(UUID id) {
        return toResponse(userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id)));
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public UserResponse updateUser(UUID id, UpdateUserRequest request, String updatedBy) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));

        var oldRole = user.getRole();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());
        user.setRole(request.role());
        user.setPhone(request.phone());

        var kcIdStr = user.getKeycloakId().toString();
        var userResource = realm().users().get(kcIdStr);
        var kcUser = userResource.toRepresentation();
        kcUser.setUsername(request.username());
        kcUser.setEmail(request.email());
        kcUser.setFirstName(request.firstName());
        kcUser.setLastName(request.lastName());
        userResource.update(kcUser);

        if (request.password() != null)
            userResource.resetPassword(passwordCredential(request.password()));
        if (request.role() != null && !request.role().equals(oldRole)) {
            var toRemove = realm().users().get(kcIdStr).roles().realmLevel().listAll().stream()
                    .filter(r -> METIER_ROLES.contains(r.getName()))
                    .toList();
            if (!toRemove.isEmpty())
                realm().users().get(kcIdStr).roles().realmLevel().remove(toRemove);
            assignRealmRole(kcIdStr, request.role());
        }

        auditService.log(userIdOrNull(updatedBy), "USER_UPDATED", "User", user.getId().toString(),
                "User updated: " + user.getUsername(), null);

        return toResponse(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public void deleteUser(UUID id, String deletedBy) {
        var user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
        user.setActive(false);
        userRepository.save(user);

        var kcIdStr = user.getKeycloakId().toString();
        var kcUser = realm().users().get(kcIdStr).toRepresentation();
        kcUser.setEnabled(false);
        realm().users().get(kcIdStr).update(kcUser);

        auditService.log(userIdOrNull(deletedBy), "USER_DELETED", "User",
                user.getId().toString(), "Account deactivated: " + user.getEmail(), null);
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public UserResponse assignRole(UUID keycloakId, String newRole, String adminId) {
        var user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException("User not found: " + keycloakId));
        user.setRole(newRole);

        var kcIdStr = keycloakId.toString();
        var realm = realm();

        var toRemove = realm.users().get(kcIdStr).roles().realmLevel().listAll().stream()
                .filter(r -> METIER_ROLES.contains(r.getName()))
                .toList();
        if (!toRemove.isEmpty())
            realm.users().get(kcIdStr).roles().realmLevel().remove(toRemove);

        try {
            var role = realm.roles().get(newRole).toRepresentation();
            realm.users().get(kcIdStr).roles().realmLevel().add(List.of(role));
        } catch (NotFoundException e) {
            throw new NotFoundException("Role '" + newRole + "' not configured in Keycloak");
        }

        auditService.log(userIdOrNull(adminId), "ROLE_ASSIGNED", "User",
                user.getId().toString(), "Role " + newRole + " assigned to " + user.getUsername(), null);

        return toResponse(user);
    }

    @CacheEvict(value = "users", allEntries = true)
    @Transactional
    public UserResponse updateUserStatus(UUID keycloakId, boolean enabled, String adminId) {
        var user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException("User not found: " + keycloakId));

        if (Boolean.TRUE.equals(user.getActive()) == enabled) {
            return toResponse(user);
        }

        user.setActive(enabled);

        var kcIdStr = keycloakId.toString();
        var kcUser = realm().users().get(kcIdStr).toRepresentation();
        kcUser.setEnabled(enabled);
        realm().users().get(kcIdStr).update(kcUser);

        var action = enabled ? "ACCOUNT_ENABLED" : "ACCOUNT_DISABLED";
        auditService.log(userIdOrNull(adminId), action, "User",
                user.getId().toString(), "Account " + user.getUsername() + ": " + (enabled ? "enabled" : "disabled"), null);

        return toResponse(user);
    }

    @Transactional
    public void sendPasswordReset(UUID keycloakId, String adminId) {
        userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException("User not found: " + keycloakId));
        realm().users().get(keycloakId.toString())
                .executeActionsEmail(List.of("UPDATE_PASSWORD"));
        auditService.log(userIdOrNull(adminId), "PASSWORD_RESET_SENT", "User",
                keycloakId.toString(), "Password reset email sent", null);
        log.info("Password reset email sent for keycloakId={}", keycloakId);
    }

    @Transactional(readOnly = true)
    public UserResponse getProfile(UUID keycloakId) {
        return toResponse(userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException("Profile not found")));
    }

    @Transactional
    public void markEmailVerified(UUID userId) {
        var user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException("User not found: " + userId));
        user.setEmailVerified(true);
        userRepository.save(user);
        log.info("Email verified for userId={}", userId);
    }

    @Transactional
    public UserResponse updateProfile(UUID keycloakId, UpdateProfileRequest request) {
        var user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException("Profile not found"));

        user.setFirstName(request.firstName());
        user.setLastName(request.lastName());

        var kcIdStr = keycloakId.toString();
        var userResource = realm().users().get(kcIdStr);
        var kcUser = userResource.toRepresentation();
        kcUser.setFirstName(request.firstName());
        kcUser.setLastName(request.lastName());
        userResource.update(kcUser);

        auditService.log(keycloakId, "PROFILE_UPDATED", "User",
                user.getId().toString(), "Profile updated: " + user.getUsername(), null);

        return toResponse(user);
    }

    @Transactional
    public void changePassword(UUID keycloakId, ChangePasswordRequest request) {
        var user = userRepository.findByKeycloakId(keycloakId)
                .orElseThrow(() -> new NotFoundException("User not found: " + keycloakId));

        verifyCurrentPassword(user.getUsername(), request.currentPassword());

        validatePasswordStrength(request.newPassword());

        var userResource = realm().users().get(keycloakId.toString());
        userResource.resetPassword(passwordCredential(request.newPassword()));

        try {
            userResource.logout();
            log.info("All sessions invalidated for keycloakId={}", keycloakId);
        } catch (Exception e) {
            log.warn("Failed to invalidate sessions for keycloakId={}: {}", keycloakId, e.getMessage());
        }

        auditService.log(keycloakId, "PASSWORD_CHANGED", "User",
                user.getId().toString(), "Password changed: " + user.getUsername(), null);
        log.info("Password changed for keycloakId={}", keycloakId);
    }

    // Uses Keycloak ROPC (Resource Owner Password Credentials) grant for password verification.
    // Requires the verification client to have "Direct Access Grants Enabled" in Keycloak.
    // Note: ROPC is deprecated in OAuth 2.1 and disabled by default in Keycloak 26+.
    private void verifyCurrentPassword(String username, String currentPassword) {
        if (keycloakProperties.verificationClientId() == null
                || keycloakProperties.verificationClientId().isBlank()) {
            throw new IllegalStateException(
                "Password verification is not configured: set KEYCLOAK_VERIFICATION_CLIENT_ID");
        }
        var tokenUrl = keycloakProperties.authServerUrl() + "/realms/"
                + keycloakProperties.realm() + "/protocol/openid-connect/token";
        var body = "client_id=" + URLEncoder.encode(keycloakProperties.verificationClientId(), StandardCharsets.UTF_8)
                + "&client_secret=" + URLEncoder.encode(keycloakProperties.verificationClientSecret(), StandardCharsets.UTF_8)
                + "&username=" + URLEncoder.encode(username, StandardCharsets.UTF_8)
                + "&password=" + URLEncoder.encode(currentPassword, StandardCharsets.UTF_8)
                + "&grant_type=password";
        try {
            var request = HttpRequest.newBuilder()
                    .uri(URI.create(tokenUrl))
                    .header("Content-Type", "application/x-www-form-urlencoded")
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .build();
            var response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new IllegalArgumentException("Current password is incorrect");
            }
        } catch (IllegalArgumentException e) {
            throw e;
        } catch (Exception e) {
            log.warn("Password verification failed for {}: {}", username, e.getMessage());
            throw new RuntimeException("Unable to verify current password");
        }
    }

    private void validatePasswordStrength(String password) {
        if (password.length() < 8)
            throw new IllegalArgumentException("Password must contain at least 8 characters");
        if (!password.matches(".*[A-Z].*"))
            throw new IllegalArgumentException("Password must contain at least one uppercase letter");
        if (!password.matches(".*[a-z].*"))
            throw new IllegalArgumentException("Password must contain at least one lowercase letter");
        if (!password.matches(".*\\d.*"))
            throw new IllegalArgumentException("Password must contain at least one digit");
        if (!password.matches(".*[^a-zA-Z0-9].*"))
            throw new IllegalArgumentException("Password must contain at least one special character");
    }

    @Transactional
    public UserResponse registerClient(CreateUserRequest request, String ip) {
        var response = createUser(new CreateUserRequest(
                request.username(), request.email(),
                request.firstName(), request.lastName(),
                request.password(), "CLIENT_USER", request.phone()), "SELF_REGISTER", ip);

        try {
            var user = userRepository.findById(response.id()).orElseThrow();
            user.setMustChangePassword(true);
            userRepository.save(user);
            return toResponse(user);
        } catch (RuntimeException e) {
            log.warn("Failed to set mustChangePassword for userId={}, cleaning up KC user", response.id());
            try {
                var realm = realm();
                realm.users().get(response.keycloakId().toString()).remove();
            } catch (Exception ignored) {
                log.warn("Failed to cleanup Keycloak user after registerClient failure");
            }
            throw e;
        }
    }

    private void assignRealmRole(String userId, String role) {
        var r = realm();
        try {
            var roleRep = r.roles().get(role).toRepresentation();
            r.users().get(userId).roles().realmLevel().add(List.of(roleRep));
        } catch (jakarta.ws.rs.NotFoundException e) {
            throw new NotFoundException("Role '" + role + "' not configured in Keycloak");
        }
    }

    private org.keycloak.admin.client.resource.RealmResource realm() {
        return keycloak.realm(keycloakProperties.realm());
    }

    private static UUID userIdOrNull(String s) {
        if (s == null) return null;
        try { return UUID.fromString(s); } catch (IllegalArgumentException e) { return null; }
    }

    private static CredentialRepresentation passwordCredential(String password) {
        var cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(password);
        cred.setTemporary(false);
        return cred;
    }

    private UserResponse toResponse(User user) {
        return new UserResponse(user.getId(), user.getKeycloakId(),
                user.getUsername(), user.getEmail(),
                user.getFirstName(), user.getLastName(),
                user.getRole(), user.getPhone(),
                user.getActive(), user.getCompanyId(),
                Boolean.TRUE.equals(user.getMustChangePassword()),
                Boolean.TRUE.equals(user.getEmailVerified()),
                user.getCreatedAt(), user.getUpdatedAt());
    }
}
