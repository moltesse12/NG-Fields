package tg.ngstars.report.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tg.ngstars.report.model.PdfTemplate;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PdfTemplateRepository extends JpaRepository<PdfTemplate, UUID> {
    List<PdfTemplate> findByTemplateTypeOrderByIsDefaultDescNameAsc(String templateType);
    Optional<PdfTemplate> findByIsDefaultTrueAndTemplateType(String templateType);
}
