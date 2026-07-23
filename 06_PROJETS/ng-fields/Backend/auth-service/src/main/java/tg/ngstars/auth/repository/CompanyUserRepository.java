package tg.ngstars.auth.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import tg.ngstars.auth.model.CompanyUser;

public interface CompanyUserRepository extends JpaRepository<CompanyUser, UUID> {

    List<CompanyUser> findByCompanyIdAndActiveTrue(UUID companyId);

    List<CompanyUser> findByCompanyId(UUID companyId);

    boolean existsByKeycloakUserId(UUID keycloakUserId);

    boolean existsByCompanyIdAndEmail(UUID companyId, String email);

    List<CompanyUser> findByKeycloakUserId(UUID keycloakUserId);
}
