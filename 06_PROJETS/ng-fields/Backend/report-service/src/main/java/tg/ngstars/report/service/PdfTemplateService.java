package tg.ngstars.report.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.ngstars.report.dto.CreatePdfTemplateRequest;
import tg.ngstars.report.dto.PdfTemplateResponse;
import tg.ngstars.report.dto.UpdatePdfTemplateRequest;
import tg.ngstars.report.model.PdfTemplate;
import tg.ngstars.report.repository.PdfTemplateRepository;

import java.util.List;
import java.util.UUID;

@Service
public class PdfTemplateService {

    private static final Logger log = LoggerFactory.getLogger(PdfTemplateService.class);

    private final PdfTemplateRepository repository;

    public PdfTemplateService(PdfTemplateRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<PdfTemplateResponse> listAll(String templateType) {
        var templates = (templateType != null)
                ? repository.findByTemplateTypeOrderByIsDefaultDescNameAsc(templateType)
                : repository.findAll();
        return templates.stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public PdfTemplateResponse getById(UUID id) {
        var template = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Template non trouve: " + id));
        return toResponse(template);
    }

    @Transactional(readOnly = true)
    public PdfTemplateResponse getDefault(String templateType) {
        var type = templateType != null ? templateType : "INTERVENTION_REPORT";
        var template = repository.findByIsDefaultTrueAndTemplateType(type)
                .orElseThrow(() -> new IllegalArgumentException("Aucun template par defaut pour le type: " + type));
        return toResponse(template);
    }

    @Transactional
    public PdfTemplateResponse create(CreatePdfTemplateRequest request, String userKeycloakId) {
        var template = new PdfTemplate();
        template.setName(request.name());
        template.setDescription(request.description());
        template.setTemplateType(request.templateType() != null ? request.templateType() : "INTERVENTION_REPORT");
        template.setConfig(request.config());
        template.setCreatedBy(userKeycloakId);

        var saved = repository.save(template);
        log.info("Template cree: {} (id={})", saved.getName(), saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public PdfTemplateResponse update(UUID id, UpdatePdfTemplateRequest request) {
        var template = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Template non trouve: " + id));

        if (request.name() != null) template.setName(request.name());
        if (request.description() != null) template.setDescription(request.description());
        if (request.config() != null) template.setConfig(request.config());
        if (request.isDefault() != null && request.isDefault()) {
            clearDefaultForType(template.getTemplateType());
            template.setIsDefault(true);
        }

        var saved = repository.save(template);
        log.info("Template mis a jour: {} (id={})", saved.getName(), saved.getId());
        return toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) {
            throw new IllegalArgumentException("Template non trouve: " + id);
        }
        repository.deleteById(id);
        log.info("Template supprime: id={}", id);
    }

    private void clearDefaultForType(String templateType) {
        repository.findByIsDefaultTrueAndTemplateType(templateType)
                .ifPresent(t -> {
                    t.setIsDefault(false);
                    repository.save(t);
                });
    }

    private PdfTemplateResponse toResponse(PdfTemplate t) {
        return new PdfTemplateResponse(
                t.getId(), t.getName(), t.getDescription(),
                t.getTemplateType(), t.getConfig(),
                t.getIsDefault(), t.getCreatedBy(),
                t.getCreatedAt(), t.getUpdatedAt()
        );
    }
}
