package tg.ngstars.auth.service;

import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.ws.rs.core.Response;
import tg.ngstars.auth.config.KeycloakProperties;
import tg.ngstars.auth.dto.AddCompanyUserRequest;
import tg.ngstars.auth.dto.CompanyResponse;
import tg.ngstars.auth.dto.CompanyUserResponse;
import tg.ngstars.auth.dto.CreateCompanyRequest;
import tg.ngstars.auth.dto.UpdateCompanyRequest;
import tg.ngstars.auth.dto.UpdateCompanyUserRoleRequest;
import tg.ngstars.common.exception.ConflictException;
import tg.ngstars.common.exception.NotFoundException;
import tg.ngstars.auth.model.Company;
import tg.ngstars.auth.model.CompanyAccessLog;
import tg.ngstars.auth.model.CompanyUser;
import tg.ngstars.auth.model.User;
import tg.ngstars.auth.repository.CompanyAccessLogRepository;
import tg.ngstars.auth.repository.CompanyRepository;
import tg.ngstars.auth.repository.CompanyUserRepository;
import tg.ngstars.auth.repository.UserRepository;

import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;

@Service
@Transactional(readOnly = true)
public class CompanyService {

    private static final Logger log = LoggerFactory.getLogger(CompanyService.class);
    private static final List<String> CLIENT_ROLES = List.of("CLIENT_ADMIN", "CLIENT_USER", "CLIENT_VIEWER");

    private final CompanyRepository companyRepository;
    private final CompanyUserRepository companyUserRepository;
    private final CompanyAccessLogRepository accessLogRepository;
    private final UserRepository userRepository;
    private final AuditService auditService;
    private final EmailService emailService;
    private final Keycloak keycloak;
    private final KeycloakProperties keycloakProperties;

    public CompanyService(CompanyRepository companyRepository,
            CompanyUserRepository companyUserRepository,
            CompanyAccessLogRepository accessLogRepository,
            UserRepository userRepository,
            AuditService auditService,
            EmailService emailService,
            Keycloak keycloak, KeycloakProperties keycloakProperties) {
        this.companyRepository = companyRepository;
        this.companyUserRepository = companyUserRepository;
        this.accessLogRepository = accessLogRepository;
        this.userRepository = userRepository;
        this.auditService = auditService;
        this.emailService = emailService;
        this.keycloak = keycloak;
        this.keycloakProperties = keycloakProperties;
    }

    // ── Company CRUD ──────────────────────────────────────────

    @CacheEvict(value = "companies", allEntries = true)
    @Transactional
    public CompanyResponse createCompany(CreateCompanyRequest request, String createdBy) {
        if (companyRepository.existsByName(request.name()))
            throw new ConflictException("Company name '" + request.name() + "' already exists");

        var company = new Company();
        company.setName(request.name());
        company.setEmail(request.email());
        company.setPhone(request.phone());
        company.setAddress(request.address());
        company.setContactName(request.contactName());
        company.setContactPhone(request.contactPhone());
        company.setActive(true);

        var saved = companyRepository.save(company);
        auditService.log(parseUuid(createdBy), "COMPANY_CREATED", "Company", saved.getId().toString(),
                "Company created: " + saved.getName(), null);
        log.info("Company created: {} (id={})", saved.getName(), saved.getId());
        return toCompanyResponse(saved);
    }

    @Cacheable(value = "companies", key = "'all_' + #pageable.pageNumber")
    public Page<CompanyResponse> getAllCompanies(Pageable pageable) {
        return companyRepository.findAll(pageable).map(this::toCompanyResponse);
    }

    @Cacheable(value = "companies", key = "'active'")
    public List<CompanyResponse> getActiveCompanies() {
        return companyRepository.findByActiveTrue().stream().map(this::toCompanyResponse).toList();
    }

    @Cacheable(value = "companies", key = "#id")
    public CompanyResponse getCompany(UUID id) {
        return toCompanyResponse(findCompanyOrThrow(id));
    }

    @CacheEvict(value = "companies", allEntries = true)
    @Transactional
    public CompanyResponse updateCompany(UUID id, UpdateCompanyRequest request, String updatedBy) {
        var company = findCompanyOrThrow(id);
        if (request.name() != null) company.setName(request.name());
        if (request.email() != null) company.setEmail(request.email());
        if (request.phone() != null) company.setPhone(request.phone());
        if (request.address() != null) company.setAddress(request.address());
        if (request.contactName() != null) company.setContactName(request.contactName());
        if (request.contactPhone() != null) company.setContactPhone(request.contactPhone());

        var saved = companyRepository.save(company);
        auditService.log(parseUuid(updatedBy), "COMPANY_UPDATED", "Company", saved.getId().toString(),
                "Company updated: " + saved.getName(), null);
        return toCompanyResponse(saved);
    }

    @CacheEvict(value = "companies", allEntries = true)
    @Transactional
    public void deactivateCompany(UUID id, String deletedBy) {
        var company = findCompanyOrThrow(id);
        company.setActive(false);
        companyRepository.save(company);
        auditService.log(parseUuid(deletedBy), "COMPANY_DEACTIVATED", "Company", id.toString(),
                "Company deactivated: " + company.getName(), null);
    }

    @CacheEvict(value = "companies", allEntries = true)
    @Transactional
    public CompanyResponse reactivateCompany(UUID id, String reactivatedBy) {
        var company = findCompanyOrThrow(id);
        company.setActive(true);
        companyRepository.save(company);
        auditService.log(parseUuid(reactivatedBy), "COMPANY_REACTIVATED", "Company", id.toString(),
                "Company reactivated: " + company.getName(), null);
        return toCompanyResponse(company);
    }

    // ── Company Users ─────────────────────────────────────────

    @CacheEvict(value = "users", key = "#companyId")
    @Transactional
    public CompanyUserResponse addCompanyUser(UUID companyId, AddCompanyUserRequest request, String createdBy) {
        var company = findCompanyOrThrow(companyId);
        if (companyUserRepository.existsByCompanyIdAndEmail(companyId, request.email()))
            throw new ConflictException("User '" + request.email() + "' already belongs to this company");

        var kcUser = new UserRepresentation();
        kcUser.setUsername(request.email());
        kcUser.setEmail(request.email());
        kcUser.setFirstName(request.firstName());
        kcUser.setLastName(request.lastName());
        kcUser.setEnabled(true);

        String tempPassword = generateTempPassword();
        kcUser.setCredentials(List.of(passwordCredential(tempPassword)));

        UUID keycloakId = null;
        var realm = realm();
        try (Response response = realm.users().create(kcUser)) {
            if (response.getStatus() == 409) {
                var existing = realm.users().search(request.email()).stream().findFirst();
                if (existing.isPresent()) {
                    keycloakId = UUID.fromString(existing.get().getId());
                } else {
                    throw new ConflictException("Keycloak user already exists: " + request.email());
                }
            } else if (response.getStatus() != 201) {
                throw new RuntimeException("Keycloak user creation failed: " + response.getStatus());
            }
            if (keycloakId == null) {
                var location = response.getLocation();
                keycloakId = UUID.fromString(location.getPath().substring(location.getPath().lastIndexOf('/') + 1));
            }

            assignRealmRole(keycloakId.toString(), request.role());
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

        try {
            var companyUser = new CompanyUser();
            companyUser.setCompany(company);
            companyUser.setKeycloakUserId(keycloakId);
            companyUser.setEmail(request.email());
            companyUser.setFirstName(request.firstName());
            companyUser.setLastName(request.lastName());
            companyUser.setRole(request.role());
            companyUser.setActive(true);

            var saved = companyUserRepository.save(companyUser);

            var user = new User();
            user.setKeycloakId(keycloakId);
            user.setUsername(request.email());
            user.setEmail(request.email());
            user.setFirstName(request.firstName());
            user.setLastName(request.lastName());
            user.setRole(request.role());
            user.setCompanyId(companyId);
            user.setMustChangePassword(true);
            user.setActive(true);
            userRepository.save(user);

            auditService.log(parseUuid(createdBy), "COMPANY_USER_ADDED", "CompanyUser", saved.getId().toString(),
                    "User " + request.email() + " added to company " + company.getName(), null);
            log.info("Company user added: {} to company {} (tempPassword sent by email)", request.email(), company.getName());

            try {
                emailService.sendCredentialsEmail(request.email(), request.firstName(), tempPassword);
            } catch (Exception e) {
                log.warn("Failed to send credentials email to {}: {}", request.email(), e.getMessage());
            }

            return toCompanyUserResponse(saved);
        } catch (RuntimeException dbEx) {
            if (keycloakId != null) {
                try {
                    realm().users().get(keycloakId.toString()).remove();
                    log.warn("Keycloak user {} cleaned up after DB failure", keycloakId);
                } catch (Exception ignored) {
                    log.warn("Failed to cleanup Keycloak user {}", keycloakId, ignored);
                }
            }
            throw dbEx;
        }
    }

    @Cacheable(value = "users", key = "#companyId")
    public List<CompanyUserResponse> getCompanyUsers(UUID companyId) {
        findCompanyOrThrow(companyId);
        return companyUserRepository.findByCompanyId(companyId).stream()
                .map(this::toCompanyUserResponse).toList();
    }

    @CacheEvict(value = "users", key = "#companyId")
    @Transactional
    public CompanyUserResponse updateCompanyUserRole(UUID companyId, UUID userId, UpdateCompanyUserRoleRequest request, String updatedBy) {
        findCompanyOrThrow(companyId);
        var companyUser = companyUserRepository.findById(userId)
                .filter(cu -> cu.getCompany().getId().equals(companyId))
                .orElseThrow(() -> new NotFoundException("Company user not found: " + userId));

        companyUser.setRole(request.role());

        if (companyUser.getKeycloakUserId() != null) {
            var realm = realm();
            var toRemove = realm.users().get(companyUser.getKeycloakUserId().toString())
                    .roles().realmLevel().listAll().stream()
                    .filter(r -> CLIENT_ROLES.contains(r.getName()))
                    .toList();
            if (!toRemove.isEmpty())
                realm.users().get(companyUser.getKeycloakUserId().toString())
                        .roles().realmLevel().remove(toRemove);
            assignRealmRole(companyUser.getKeycloakUserId().toString(), request.role());
        }

        var saved = companyUserRepository.save(companyUser);
        auditService.log(parseUuid(updatedBy), "COMPANY_USER_ROLE_UPDATED", "CompanyUser", saved.getId().toString(),
                "Role changed to " + request.role() + " for " + saved.getEmail(), null);
        return toCompanyUserResponse(saved);
    }

    @CacheEvict(value = "users", key = "#companyId")
    @Transactional
    public void deactivateCompanyUser(UUID companyId, UUID userId, String deletedBy) {
        findCompanyOrThrow(companyId);
        var companyUser = companyUserRepository.findById(userId)
                .filter(cu -> cu.getCompany().getId().equals(companyId))
                .orElseThrow(() -> new NotFoundException("Company user not found: " + userId));

        companyUser.setActive(false);
        companyUserRepository.save(companyUser);

        if (companyUser.getKeycloakUserId() != null) {
            var realm = realm();
            var kcUser = realm.users().get(companyUser.getKeycloakUserId().toString()).toRepresentation();
            kcUser.setEnabled(false);
            realm.users().get(companyUser.getKeycloakUserId().toString()).update(kcUser);

            userRepository.findByKeycloakId(companyUser.getKeycloakUserId()).ifPresent(u -> {
                u.setActive(false);
                userRepository.save(u);
            });
        }

        auditService.log(parseUuid(deletedBy), "COMPANY_USER_DEACTIVATED", "CompanyUser", userId.toString(),
                "Company user deactivated: " + companyUser.getEmail(), null);
    }

    // ── Access Log ────────────────────────────────────────────

    @Transactional
    public void logAccess(UUID companyId, UUID companyUserId, String action, String resource, UUID resourceId, String ip) {
        var company = findCompanyOrThrow(companyId);
        var logEntry = new CompanyAccessLog();
        logEntry.setCompany(company);
        if (companyUserId != null) {
            companyUserRepository.findById(companyUserId).ifPresent(logEntry::setUser);
        }
        logEntry.setAction(action);
        logEntry.setResource(resource);
        logEntry.setResourceId(resourceId);
        if (ip != null) {
            try { logEntry.setIpAddress(java.net.InetAddress.getByName(ip)); }
            catch (Exception e) { log.warn("Invalid IP address in access log: {}", ip); }
        }
        accessLogRepository.save(logEntry);
    }

    // ── Helpers ───────────────────────────────────────────────

    private Company findCompanyOrThrow(UUID id) {
        return companyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Company not found: " + id));
    }

    private static String generateTempPassword() {
        var random = new java.security.SecureRandom();
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789!@#$%";
        var sb = new StringBuilder(16);
        for (int i = 0; i < 16; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private static UUID parseUuid(String value) {
        try {
            return value != null ? UUID.fromString(value) : null;
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    private static CredentialRepresentation passwordCredential(String password) {
        var cred = new CredentialRepresentation();
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(password);
        cred.setTemporary(true);
        return cred;
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

    private CompanyResponse toCompanyResponse(Company c) {
        return new CompanyResponse(
                c.getId(), c.getName(), c.getEmail(), c.getPhone(),
                c.getAddress(), c.getContactName(), c.getContactPhone(),
                c.getKeycloakOrganizationId(),
                Boolean.TRUE.equals(c.getActive()),
                c.getCreatedAt(), c.getUpdatedAt());
    }

    private CompanyUserResponse toCompanyUserResponse(CompanyUser cu) {
        return new CompanyUserResponse(
                cu.getId(),
                cu.getCompany().getId(),
                cu.getKeycloakUserId(),
                cu.getEmail(), cu.getFirstName(), cu.getLastName(),
                cu.getRole(),
                Boolean.TRUE.equals(cu.getActive()),
                cu.getCreatedAt(), cu.getUpdatedAt());
    }
}
