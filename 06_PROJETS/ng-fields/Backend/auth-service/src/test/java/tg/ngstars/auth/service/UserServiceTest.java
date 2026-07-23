package tg.ngstars.auth.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

import tg.ngstars.auth.config.KeycloakProperties;
import tg.ngstars.auth.model.User;
import tg.ngstars.auth.repository.UserRepository;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("UserService")
class UserServiceTest {

    @Mock private UserRepository userRepository;
    @Mock private AuditService auditService;
    @Mock private EmailService emailService;
    @Mock private Keycloak keycloak;
    @Mock private KeycloakProperties keycloakProperties;
    @Mock private RealmResource realmResource;
    @Mock private UsersResource usersResource;
    @Mock private UserResource userResource;
    @Mock private RolesResource rolesResource;

    @InjectMocks
    private UserService userService;

    private UUID userId;
    private UUID keycloakId;
    private User user;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        keycloakId = UUID.randomUUID();
        user = new User();
        user.setId(userId);
        user.setKeycloakId(keycloakId);
        user.setUsername("testuser");
        user.setEmail("test@ngstars.tg");
        user.setFirstName("Test");
        user.setLastName("User");
        user.setRole("TECHNICIAN");
        user.setActive(true);
    }

    private void mockKeycloak() {
        when(keycloakProperties.realm()).thenReturn("ng-fields");
        when(keycloak.realm("ng-fields")).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(keycloakId.toString())).thenReturn(userResource);
        when(userResource.toRepresentation()).thenReturn(new UserRepresentation());
        when(realmResource.roles()).thenReturn(rolesResource);
    }

    @Nested
    @DisplayName("getProfile()")
    class GetProfile {

        @Test
        @DisplayName("Retourne le profil quand trouvé")
        void getProfile_found() {
            when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(user));
            var response = userService.getProfile(keycloakId);
            assertNotNull(response);
            assertEquals("testuser", response.username());
        }

        @Test
        @DisplayName("Lance NotFoundException si introuvable")
        void getProfile_notFound_throws() {
            when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.empty());
            assertThrows(tg.ngstars.common.exception.NotFoundException.class,
                    () -> userService.getProfile(keycloakId));
        }
    }

    @Nested
    @DisplayName("getUser()")
    class GetUser {

        @Test
        @DisplayName("Retourne l'utilisateur quand trouvé")
        void getUser_found() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            var response = userService.getUser(userId);
            assertNotNull(response);
            assertEquals("test@ngstars.tg", response.email());
        }

        @Test
        @DisplayName("Lance NotFoundException si introuvable")
        void getUser_notFound_throws() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());
            assertThrows(tg.ngstars.common.exception.NotFoundException.class,
                    () -> userService.getUser(userId));
        }
    }

    @Nested
    @DisplayName("deleteUser()")
    class DeleteUser {

        @Test
        @DisplayName("Désactive l'utilisateur dans la DB et Keycloak")
        void deleteUser_success() {
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            mockKeycloak();

            userService.deleteUser(userId, "admin-id");

            assertFalse(user.getActive());
            verify(auditService).log(any(), eq("USER_DELETED"), anyString(), anyString(), anyString(), isNull());
        }

        @Test
        @DisplayName("Lance NotFoundException si introuvable")
        void deleteUser_notFound_throws() {
            when(userRepository.findById(userId)).thenReturn(Optional.empty());
            assertThrows(tg.ngstars.common.exception.NotFoundException.class,
                    () -> userService.deleteUser(userId, "admin-id"));
        }
    }

    @Nested
    @DisplayName("updateUserStatus()")
    class UpdateUserStatus {

        @Test
        @DisplayName("Active/désactive l'utilisateur")
        void updateUserStatus_success() {
            when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(user));
            mockKeycloak();

            var response = userService.updateUserStatus(keycloakId, false, "admin-id");

            assertFalse(user.getActive());
            verify(auditService).log(any(), eq("ACCOUNT_DISABLED"), anyString(), anyString(), anyString(), isNull());
        }
    }

    @Nested
    @DisplayName("sendPasswordReset()")
    class SendPasswordReset {

        @Test
        @DisplayName("Envoie l'email de réinitialisation")
        void sendPasswordReset_success() {
            when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(user));
            mockKeycloak();
            when(userResource.toRepresentation()).thenReturn(new UserRepresentation());

            assertDoesNotThrow(() -> userService.sendPasswordReset(keycloakId, "admin-id"));
            verify(userResource).executeActionsEmail(List.of("UPDATE_PASSWORD"));
        }
    }

    @Nested
    @DisplayName("assignRole()")
    class AssignRole {

        @Test
        @DisplayName("Change le rôle de l'utilisateur")
        void assignRole_success() {
            when(userRepository.findByKeycloakId(keycloakId)).thenReturn(Optional.of(user));
            mockKeycloak();
            var roleRep = new RoleRepresentation();
            when(rolesResource.get("ADMIN")).thenReturn(mock(org.keycloak.admin.client.resource.RoleResource.class));
            when(rolesResource.get("ADMIN").toRepresentation()).thenReturn(roleRep);
            var roleMappingResource = mock(org.keycloak.admin.client.resource.RoleMappingResource.class);
            var roleScopeResource = mock(org.keycloak.admin.client.resource.RoleScopeResource.class);
            when(userResource.roles()).thenReturn(roleMappingResource);
            when(roleMappingResource.realmLevel()).thenReturn(roleScopeResource);

            var response = userService.assignRole(keycloakId, "ADMIN", "admin-id");

            assertEquals("ADMIN", response.role());
            verify(auditService).log(any(), eq("ROLE_ASSIGNED"), anyString(), anyString(), anyString(), isNull());
        }
    }

    @Nested
    @DisplayName("getAllUsers()")
    class GetAllUsers {

        @Test
        @DisplayName("Retourne les utilisateurs paginés")
        void getAllUsers_paged() {
            var page = new org.springframework.data.domain.PageImpl<>(List.of(user));
            when(userRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

            var result = userService.getAllUsers(org.springframework.data.domain.PageRequest.of(0, 10));

            assertEquals(1, result.getContent().size());
            assertEquals("testuser", result.getContent().getFirst().username());
        }
    }
}
