package tg.ngstars.report.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.ngstars.report.dto.CreateEmailTemplateRequest;
import tg.ngstars.report.dto.EmailTemplateResponse;
import tg.ngstars.report.dto.UpdateEmailTemplateRequest;
import tg.ngstars.report.model.EmailTemplate;
import tg.ngstars.report.repository.EmailTemplateRepository;

import java.util.List;
import java.util.UUID;

@Service
public class EmailTemplateService {

    private static final Logger log = LoggerFactory.getLogger(EmailTemplateService.class);
    private final EmailTemplateRepository repository;

    public EmailTemplateService(EmailTemplateRepository repository) {
        this.repository = repository;
    }

    @Transactional(readOnly = true)
    public List<EmailTemplateResponse> listAll() {
        return repository.findAll().stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public EmailTemplateResponse getById(UUID id) {
        return toResponse(repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Template email non trouve: " + id)));
    }

    @Transactional(readOnly = true)
    public EmailTemplateResponse getByKey(String key) {
        return toResponse(repository.findByTemplateKey(key)
                .orElseThrow(() -> new IllegalArgumentException("Template email non trouve: " + key)));
    }

    @Transactional
    public EmailTemplateResponse create(CreateEmailTemplateRequest request, String userKeycloakId) {
        var tpl = new EmailTemplate();
        tpl.setName(HtmlSanitizer.sanitizePlainText(request.name()));
        tpl.setDescription(HtmlSanitizer.sanitizePlainText(request.description()));
        tpl.setTemplateKey(request.templateKey());
        tpl.setSubject(HtmlSanitizer.sanitizePlainText(request.subject()));
        tpl.setBodyHtml(HtmlSanitizer.sanitize(request.bodyHtml()));
        tpl.setCreatedBy(userKeycloakId);
        var saved = repository.save(tpl);
        log.info("Email template cree: {} (key={})", saved.getName(), saved.getTemplateKey());
        return toResponse(saved);
    }

    @Transactional
    public EmailTemplateResponse update(UUID id, UpdateEmailTemplateRequest request) {
        var tpl = repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Template email non trouve: " + id));
        if (request.name() != null) tpl.setName(HtmlSanitizer.sanitizePlainText(request.name()));
        if (request.description() != null) tpl.setDescription(HtmlSanitizer.sanitizePlainText(request.description()));
        if (request.subject() != null) tpl.setSubject(HtmlSanitizer.sanitizePlainText(request.subject()));
        if (request.bodyHtml() != null) tpl.setBodyHtml(HtmlSanitizer.sanitize(request.bodyHtml()));
        if (request.isActive() != null) tpl.setIsActive(request.isActive());
        var saved = repository.save(tpl);
        log.info("Email template mis a jour: {} (key={})", saved.getName(), saved.getTemplateKey());
        return toResponse(saved);
    }

    @Transactional
    public void delete(UUID id) {
        if (!repository.existsById(id)) throw new IllegalArgumentException("Template email non trouve: " + id);
        repository.deleteById(id);
        log.info("Email template supprime: id={}", id);
    }

    private EmailTemplateResponse toResponse(EmailTemplate t) {
        return new EmailTemplateResponse(t.getId(), t.getName(), t.getDescription(),
                t.getTemplateKey(), t.getSubject(), t.getBodyHtml(),
                t.getIsActive(), t.getCreatedBy(), t.getCreatedAt(), t.getUpdatedAt());
    }
}
