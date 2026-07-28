package tg.ngstars.report.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.ngstars.report.dto.CreatePdfTemplateRequest;
import tg.ngstars.report.dto.PdfTemplateResponse;
import tg.ngstars.report.dto.UpdatePdfTemplateRequest;
import tg.ngstars.report.model.PdfTemplate;
import tg.ngstars.report.repository.PdfTemplateRepository;

import tools.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.UUID;

@Service
public class PdfTemplateService {

    private static final Logger log = LoggerFactory.getLogger(PdfTemplateService.class);

    private final PdfTemplateRepository repository;
    private final ObjectMapper objectMapper;

    public PdfTemplateService(PdfTemplateRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @Cacheable(value = "pdfTemplates", key = "#templateType")
    @Transactional(readOnly = true)
    public List<PdfTemplateResponse> listAll(String templateType) {
        var templates = (templateType != null)
                ? repository.findByTemplateTypeOrderByIsDefaultDescNameAsc(templateType)
                : repository.findAll();
        return templates.stream().map(this::toResponse).toList();
    }

    @Cacheable(value = "pdfTemplates", key = "#id")
    @Transactional(readOnly = true)
    public PdfTemplateResponse getById(UUID id) {
        var template = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Template non trouve: " + id));
        return toResponse(template);
    }

    @Cacheable(value = "pdfTemplates", key = "'default_' + #templateType")
    @Transactional(readOnly = true)
    public PdfTemplateResponse getDefault(String templateType) {
        var type = templateType != null ? templateType : "INTERVENTION_REPORT";
        var template = repository.findByIsDefaultTrueAndTemplateType(type)
                .orElseThrow(() -> new IllegalArgumentException("Aucun template par defaut pour le type: " + type));
        return toResponse(template);
    }

    @CacheEvict(value = "pdfTemplates", allEntries = true)
    @Transactional
    public PdfTemplateResponse create(CreatePdfTemplateRequest request, String userKeycloakId) {
        var template = new PdfTemplate();
        template.setName(HtmlSanitizer.sanitizePlainText(request.name()));
        template.setDescription(HtmlSanitizer.sanitizePlainText(request.description()));
        template.setTemplateType(request.templateType() != null ? request.templateType() : "INTERVENTION_REPORT");
        template.setConfig(validateJson(request.config()));
        template.setCreatedBy(userKeycloakId);

        var saved = repository.save(template);
        log.info("Template cree: {} (id={})", saved.getName(), saved.getId());
        return toResponse(saved);
    }

    @CacheEvict(value = "pdfTemplates", allEntries = true)
    @Transactional
    public PdfTemplateResponse update(UUID id, UpdatePdfTemplateRequest request) {
        var template = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Template non trouve: " + id));

        if (request.name() != null) template.setName(HtmlSanitizer.sanitizePlainText(request.name()));
        if (request.description() != null) template.setDescription(HtmlSanitizer.sanitizePlainText(request.description()));
        if (request.config() != null) template.setConfig(validateJson(request.config()));
        if (request.isDefault() != null && request.isDefault()) {
            clearDefaultForType(template.getTemplateType());
            template.setIsDefault(true);
        }

        var saved = repository.save(template);
        log.info("Template mis a jour: {} (id={})", saved.getName(), saved.getId());
        return toResponse(saved);
    }

    @CacheEvict(value = "pdfTemplates", allEntries = true)
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

    private String validateJson(String json) {
        if (json == null || json.isBlank()) return "{}";
        try {
            objectMapper.readTree(json);
            return json;
        } catch (Exception e) {
            log.warn("Invalid JSON config rejected: {}", e.getMessage());
            return "{}";
        }
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
