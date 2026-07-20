package tg.ngstars.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import jakarta.ws.rs.core.Response;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RoleMappingResource;
import org.keycloak.admin.client.resource.RoleScopeResource;
import org.keycloak.admin.client.resource.RoleResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Pageable;

import tg.ngstars.auth.config.KeycloakProperties;
import tg.ngstars.auth.dto.CreateUserRequest;
import tg.ngstars.auth.dto.ChangePasswordRequest;
import tg.ngstars.auth.dto.UpdateProfileRequest;
import tg.ngstars.auth.dto.UpdateUserRequest;
import tg.ngstars.common.exception.ConflictException;
import tg.ngstars.common.exception.NotFoundException;
import tg.ngstars.auth.model.User;
import tg.ngstars.auth.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock AuditService auditService;
    @Mock Keycloak keycloak;
    @Mock RealmResource realm;
    @Mock UsersResource usersResource;
    @Mock UserResource userResource;
    @Mock RolesResource rolesResource;
    @Mock RoleResource roleResource;
    @Mock RoleMappingResource roleMappingResource;
    @Mock RoleScopeResource roleScopeResource;

    UserService service;
    KeycloakProperties props = new KeycloakProperties("http://localhost:8088", "admin-cli", "secret", "ng-fields");

    UUID keycloakId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        service = new UserService(userRepository, auditService, keycloak, props);
        lenient().when(keycloak.realm(props.realm())).thenReturn(realm);
        lenient().when(realm.users()).thenReturn(usersResource);
        lenient().when(usersResource.get(keycloakId.toString())).thenReturn(userResource);
        lenient().when(userResource.roles()).thenReturn(roleMappingResource);
        lenient().when(roleMappingResource.realmLevel()).thenReturn(roleScopeResource);
        lenient().when(realm.roles()).thenReturn(rolesResource);
    }

    private User user() {
        var u = new User();
        u.setId(userId);
        u.setKeycloakId(keycloakId);
        u.setUsername("jdoe");
        u.setEmail("j@doe.com");
        u.setFirstName("John");
        u.setLastName("Doe");
        u.setRole("TECHNICIAN");
        u.setActive(true);
        return u;
    }

    @Test
    void createUser_shouldCreateInKeycloakAndDb() throws Exception {
        var req = new CreateUserRequest("jdoe", "j@doe.com", "John", "Doe", "pass123", "TECHNICIAN", null);

        when(userRepository.existsByUsername("jdoe")).thenReturn(false);
        when(userRepository.existsByEmail("j@doe.com")).thenReturn(false);

        var locationUri = new URI("http://localhost:8088/admin/realms/ng-fields/users/" + keycloakId);
        var response = mock(Response.class);
        when(response.getStatus()).thenReturn(201);
        when(response.getLocation()).thenReturn(locationUri);
        when(usersResource.create(any())).thenReturn(response);

        when(rolesResource.get("TECHNICIAN")).thenReturn(roleResource);
        when(roleResource.toRepresentation()).thenReturn(new RoleRepresentation());

        when(userRepository.save(any())).thenAnswer(i -> {
            var u = (User) i.getArgument(0);
            u.setId(userId);
            return u;
        });

        var result = service.createUser(req, "admin", null);

        assertEquals("jdoe", result.username());
        assertEquals("TECHNICIAN", result.role());
        verify(auditService).log(any(), eq("USER_CREATED"), eq("User"), anyString(), anyString(), isNull());
    }

    @Test
    void createUser_duplicateUsername_throwsConflict() {
        var req = new CreateUserRequest("jdoe", "j@doe.com", "John", "Doe", "pass123", "TECHNICIAN", null);
        when(userRepository.existsByUsername("jdoe")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.createUser(req, "admin", null));
    }

    @Test
    void getUser_shouldReturnUser() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user()));
        var result = service.getUser(userId);
        assertEquals("jdoe", result.username());
    }

    @Test
    void getUser_notFound_throwsNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getUser(userId));
    }

    @Test
    void getAllUsers_shouldReturnPage() {
        var page = new org.springframework.data.domain.PageImpl<>(List.of(user()));
        when(userRepository.findAll(Pageable.unpaged())).thenReturn(page);
        var result = service.getAllUsers(Pageable.unpaged());
        assertEquals(1, result.getContent().size());
    }

    @Test
    void assignRole_shouldUpdateRole() {
        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(user()));
        when(roleScopeResource.listAll()).thenReturn(List.of());
        when(rolesResource.get("MANAGER")).thenReturn(roleResource);
        when(roleResource.toRepresentation()).thenReturn(new RoleRepresentation());

        var result = service.assignRole(keycloakId, "MANAGER", "admin");
        assertEquals("MANAGER", result.role());
    }

    @Test
    void updateUserStatus_enable_shouldActivate() {
        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(user()));
        when(userResource.toRepresentation()).thenReturn(new UserRepresentation());

        var result = service.updateUserStatus(keycloakId, true, "admin");

        assertTrue(result.active());
        verify(auditService).log(any(), eq("ACCOUNT_ENABLED"), eq("User"), anyString(), anyString(), isNull());
    }

    @Test
    void getProfile_shouldReturnByKeycloakId() {
        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(user()));
        var result = service.getProfile(keycloakId);
        assertEquals("jdoe", result.username());
    }

    @Test
    void updateProfile_shouldUpdateNames() {
        var request = new UpdateProfileRequest("Jane", "Smith");
        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(user()));
        when(userResource.toRepresentation()).thenReturn(new UserRepresentation());

        var result = service.updateProfile(keycloakId, request);

        assertEquals("Jane", result.firstName());
        assertEquals("Smith", result.lastName());
    }

    @Test
    void deleteUser_shouldDisable() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user()));
        when(userResource.toRepresentation()).thenReturn(new UserRepresentation());

        service.deleteUser(userId, "admin");

        verify(auditService).log(any(), eq("USER_DELETED"), eq("User"), anyString(), contains("desactive"), isNull());
    }

    @Test
    void registerClient_createsWithClientPortalRole() throws Exception {
        var req = new CreateUserRequest("client1", "c@test.com", "Client", "One", "pass123", "ADMIN", null);

        when(userRepository.existsByUsername("client1")).thenReturn(false);
        when(userRepository.existsByEmail("c@test.com")).thenReturn(false);

        var locationUri = new URI("http://localhost:8088/admin/realms/ng-fields/users/" + keycloakId);
        var response = mock(Response.class);
        when(response.getStatus()).thenReturn(201);
        when(response.getLocation()).thenReturn(locationUri);
        when(usersResource.create(any())).thenReturn(response);

        when(rolesResource.get("CLIENT_PORTAL")).thenReturn(roleResource);
        when(roleResource.toRepresentation()).thenReturn(new RoleRepresentation());

        when(userRepository.save(any())).thenAnswer(i -> {
            var u = (User) i.getArgument(0);
            u.setId(userId);
            return u;
        });

        var result = service.registerClient(req, "127.0.0.1");

        assertEquals("CLIENT_PORTAL", result.role());
    }

    @Test
    void createUser_duplicateEmail_throwsConflict() {
        var req = new CreateUserRequest("jdoe", "existing@test.com", "John", "Doe", "pass123", "TECHNICIAN", null);
        when(userRepository.existsByUsername("jdoe")).thenReturn(false);
        when(userRepository.existsByEmail("existing@test.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.createUser(req, "admin", null));
    }

    @Test
    void updateUser_shouldUpdateFields() {
        var req = new UpdateUserRequest("jdoe", "jane@test.com", "Jane", "Doe", null, "MANAGER", null);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user()));
        when(userResource.toRepresentation()).thenReturn(new UserRepresentation());

        var result = service.updateUser(userId, req, "admin");
        assertEquals("Jane", result.firstName());
    }

    @Test
    void updateUser_notFound_throwsNotFound() {
        var req = new UpdateUserRequest("jdoe", "jane@test.com", "Jane", "Doe", null, "MANAGER", null);
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.updateUser(userId, req, "admin"));
    }

    @Test
    void updateUserStatus_disable_shouldDeactivate() {
        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(user()));
        when(userResource.toRepresentation()).thenReturn(new UserRepresentation());

        var result = service.updateUserStatus(keycloakId, false, "admin");
        assertFalse(result.active());
    }

    @Test
    void sendPasswordReset_shouldExecute() {
        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(user()));
        assertDoesNotThrow(() -> service.sendPasswordReset(keycloakId, "admin"));
    }

    @Test
    void sendPasswordReset_notFound_throwsNotFound() {
        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.sendPasswordReset(keycloakId, "admin"));
    }

    @Test
    void assignRole_notFound_throwsNotFound() {
        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.assignRole(keycloakId, "MANAGER", "admin"));
    }

    @Test
    void getProfile_notFound_throwsNotFound() {
        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getProfile(keycloakId));
    }

    @Test
    void deleteUser_notFound_throwsNotFound() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.deleteUser(userId, "admin"));
    }

    @Test
    void changePassword_shouldSucceed() {
        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(user()));
        var request = new ChangePasswordRequest("OldPass1!", "NewPassw0rd!");
        service.changePassword(keycloakId, request);
        verify(userResource).resetPassword(any());
    }

    @Test
    void changePassword_weakPassword_shouldThrow() {
        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(user()));
        var request = new ChangePasswordRequest("OldPass1!", "weak");
        assertThrows(IllegalArgumentException.class,
                () -> service.changePassword(keycloakId, request));
    }

    @Test
    void changePassword_noUppercase_shouldThrow() {
        when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(user()));
        var request = new ChangePasswordRequest("OldPass1!", "alllowercase1!");
        assertThrows(IllegalArgumentException.class,
                () -> service.changePassword(keycloakId, request));
    }

    @Test
    void changePassword_userNotFound_shouldThrow() {
        when(userRepository.findByKeycloakId(any(UUID.class))).thenReturn(Optional.empty());
        var request = new ChangePasswordRequest("OldPass1!", "NewPassw0rd!");
        assertThrows(NotFoundException.class,
                () -> service.changePassword(UUID.randomUUID(), request));
    }

    @Test
    void createUser_keycloak409_fallbackToSearch() throws Exception {
        var req = new CreateUserRequest("jdoe", "j@doe.com", "John", "Doe", "pass123", "TECHNICIAN", null);
        when(userRepository.existsByUsername("jdoe")).thenReturn(false);
        when(userRepository.existsByEmail("j@doe.com")).thenReturn(false);

        var failResponse = mock(Response.class);
        when(failResponse.getStatus()).thenReturn(409);
        when(usersResource.create(any())).thenReturn(failResponse);

        var existingUser = new UserRepresentation();
        existingUser.setId(keycloakId.toString());
        when(usersResource.search("jdoe")).thenReturn(List.of(existingUser));

        when(rolesResource.get("TECHNICIAN")).thenReturn(roleResource);
        when(roleResource.toRepresentation()).thenReturn(new RoleRepresentation());
        when(userRepository.save(any())).thenAnswer(i -> {
            var u = (User) i.getArgument(0);
            u.setId(userId);
            return u;
        });

        var result = service.createUser(req, "admin", null);
        assertEquals("jdoe", result.username());
    }
}
