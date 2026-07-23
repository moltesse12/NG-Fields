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

import tg.ngstars.auth.config.KeycloakProperties;
import tg.ngstars.auth.dto.AddCompanyUserRequest;
import tg.ngstars.auth.dto.CreateCompanyRequest;
import tg.ngstars.auth.dto.UpdateCompanyRequest;
import tg.ngstars.auth.dto.UpdateCompanyUserRoleRequest;
import tg.ngstars.auth.model.Company;
import tg.ngstars.auth.model.CompanyUser;
import tg.ngstars.auth.repository.CompanyAccessLogRepository;
import tg.ngstars.auth.repository.CompanyRepository;
import tg.ngstars.auth.repository.CompanyUserRepository;
import tg.ngstars.auth.repository.UserRepository;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
@DisplayName("CompanyService")
class CompanyServiceTest {

    @Mock private CompanyRepository companyRepository;
    @Mock private CompanyUserRepository companyUserRepository;
    @Mock private CompanyAccessLogRepository accessLogRepository;
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
    private CompanyService companyService;

    private UUID companyId;
    private UUID userId;
    private Company company;
    private CompanyUser companyUser;

    @BeforeEach
    void setUp() {
        companyId = UUID.randomUUID();
        userId = UUID.randomUUID();

        company = new Company();
        company.setId(companyId);
        company.setName("NG-STARs SARL");
        company.setEmail("contact@ngstars.tg");
        company.setActive(true);

        companyUser = new CompanyUser();
        companyUser.setId(userId);
        companyUser.setCompany(company);
        companyUser.setEmail("user@ngstars.tg");
        companyUser.setFirstName("User");
        companyUser.setLastName("Test");
        companyUser.setRole("CLIENT_ADMIN");
        companyUser.setActive(true);
    }

    private void mockKeycloak() {
        when(keycloakProperties.realm()).thenReturn("ng-fields");
        when(keycloak.realm("ng-fields")).thenReturn(realmResource);
        when(realmResource.users()).thenReturn(usersResource);
        when(usersResource.get(anyString())).thenReturn(userResource);
        when(realmResource.roles()).thenReturn(rolesResource);
        when(rolesResource.get(anyString())).thenReturn(mock(org.keycloak.admin.client.resource.RoleResource.class));
    }

    @Nested
    @DisplayName("createCompany()")
    class CreateCompany {

        @Test
        @DisplayName("Crée une entreprise avec succès")
        void createCompany_success() {
            when(companyRepository.existsByName("NG-STARs")).thenReturn(false);
            when(companyRepository.save(any(Company.class))).thenAnswer(inv -> {
                var c = inv.getArgument(0, Company.class);
                c.setId(companyId);
                return c;
            });

            var request = new CreateCompanyRequest(
                    "NG-STARs", "ng@stars.tg", "+22890111111",
                    "Lomé", "Contact", "+22890222222");

            var response = companyService.createCompany(request, "admin-id");

            assertNotNull(response);
            assertEquals("NG-STARs", response.name());
            verify(auditService).log(any(), eq("COMPANY_CREATED"), anyString(), anyString(), anyString(), isNull());
        }

        @Test
        @DisplayName("Rejette si le nom existe déjà")
        void createCompany_duplicateName_throws() {
            when(companyRepository.existsByName("NG-STARs")).thenReturn(true);
            var request = new CreateCompanyRequest(
                    "NG-STARs", "ng@stars.tg", null, null, null, null);

            assertThrows(tg.ngstars.auth.exception.ConflictException.class,
                    () -> companyService.createCompany(request, "admin-id"));
        }
    }

    @Nested
    @DisplayName("getAllCompanies()")
    class GetAllCompanies {

        @Test
        @DisplayName("Retourne les entreprises paginées")
        void getAllCompanies_paged() {
            var page = new org.springframework.data.domain.PageImpl<>(List.of(company));
            when(companyRepository.findAll(any(org.springframework.data.domain.Pageable.class))).thenReturn(page);

            var result = companyService.getAllCompanies(
                    org.springframework.data.domain.PageRequest.of(0, 10));

            assertEquals(1, result.getContent().size());
        }
    }

    @Nested
    @DisplayName("getActiveCompanies()")
    class GetActiveCompanies {

        @Test
        @DisplayName("Retourne uniquement les entreprises actives")
        void getActiveCompanies_onlyActive() {
            when(companyRepository.findByActiveTrue()).thenReturn(List.of(company));

            var result = companyService.getActiveCompanies();

            assertEquals(1, result.size());
            assertTrue(result.getFirst().active());
        }
    }

    @Nested
    @DisplayName("updateCompany()")
    class UpdateCompany {

        @Test
        @DisplayName("Met à jour l'entreprise")
        void updateCompany_success() {
            when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
            when(companyRepository.save(any(Company.class))).thenAnswer(inv -> inv.getArgument(0));

            var request = new UpdateCompanyRequest(
                    "NG-STARs Updated", null, "+22899887766", null, null, null);

            var response = companyService.updateCompany(companyId, request, "admin-id");

            assertEquals("NG-STARs Updated", response.name());
        }
    }

    @Nested
    @DisplayName("deactivateCompany()")
    class DeactivateCompany {

        @Test
        @DisplayName("Désactive l'entreprise")
        void deactivateCompany_success() {
            when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
            when(companyRepository.save(any(Company.class))).thenAnswer(inv -> inv.getArgument(0));

            companyService.deactivateCompany(companyId, "admin-id");

            assertFalse(company.getActive());
            verify(auditService).log(any(), eq("COMPANY_DEACTIVATED"), anyString(), anyString(), anyString(), isNull());
        }

        @Test
        @DisplayName("Lance NotFoundException si introuvable")
        void deactivateCompany_notFound_throws() {
            when(companyRepository.findById(companyId)).thenReturn(Optional.empty());
            assertThrows(tg.ngstars.auth.exception.NotFoundException.class,
                    () -> companyService.deactivateCompany(companyId, "admin-id"));
        }
    }

    @Nested
    @DisplayName("getCompanyUsers()")
    class GetCompanyUsers {

        @Test
        @DisplayName("Retourne les utilisateurs de l'entreprise")
        void getCompanyUsers_success() {
            when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
            when(companyUserRepository.findByCompanyId(companyId)).thenReturn(List.of(companyUser));

            var result = companyService.getCompanyUsers(companyId);

            assertEquals(1, result.size());
            assertEquals("user@ngstars.tg", result.getFirst().email());
        }
    }

    @Nested
    @DisplayName("updateCompanyUserRole()")
    class UpdateCompanyUserRole {

        @Test
        @DisplayName("Change le rôle de l'utilisateur entreprise")
        void updateCompanyUserRole_success() {
            when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
            when(companyUserRepository.findById(userId)).thenReturn(Optional.of(companyUser));
            when(companyUserRepository.save(any(CompanyUser.class))).thenAnswer(inv -> inv.getArgument(0));
            mockKeycloak();

            var request = new UpdateCompanyUserRoleRequest("CLIENT_USER");
            var response = companyService.updateCompanyUserRole(companyId, userId, request, "admin-id");

            assertEquals("CLIENT_USER", response.role());
        }
    }

    @Nested
    @DisplayName("deactivateCompanyUser()")
    class DeactivateCompanyUser {

        @Test
        @DisplayName("Désactive l'utilisateur entreprise")
        void deactivateCompanyUser_success() {
            when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));
            when(companyUserRepository.findById(userId)).thenReturn(Optional.of(companyUser));
            when(companyUserRepository.save(any(CompanyUser.class))).thenAnswer(inv -> inv.getArgument(0));
            mockKeycloak();
            when(userResource.toRepresentation()).thenReturn(new UserRepresentation());

            companyService.deactivateCompanyUser(companyId, userId, "admin-id");

            assertFalse(companyUser.getActive());
            verify(auditService).log(any(), eq("COMPANY_USER_DEACTIVATED"), anyString(), anyString(), anyString(), isNull());
        }
    }

    @Nested
    @DisplayName("logAccess()")
    class LogAccess {

        @Test
        @DisplayName("Enregistre un log d'accès")
        void logAccess_success() {
            when(companyRepository.findById(companyId)).thenReturn(Optional.of(company));

            companyService.logAccess(companyId, userId, "READ", "Intervention", UUID.randomUUID(), "127.0.0.1");

            verify(accessLogRepository).save(any());
        }
    }
}
