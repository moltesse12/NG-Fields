package tg.ngstars.auth.repository;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import tg.ngstars.auth.model.CompanyAccessLog;

public interface CompanyAccessLogRepository extends JpaRepository<CompanyAccessLog, UUID> {

    List<CompanyAccessLog> findByCompanyIdOrderByCreatedAtDesc(UUID companyId);
}
