package tg.ngstars.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tg.ngstars.report.model.EmailTemplate;

import java.util.Optional;
import java.util.UUID;

public interface EmailTemplateRepository extends JpaRepository<EmailTemplate, UUID> {
    Optional<EmailTemplate> findByTemplateKey(String templateKey);
    Optional<EmailTemplate> findByTemplateKeyAndIsActiveTrue(String templateKey);
}
