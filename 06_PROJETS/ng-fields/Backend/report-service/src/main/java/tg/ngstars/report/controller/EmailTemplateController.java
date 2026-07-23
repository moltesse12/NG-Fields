package tg.ngstars.report.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import tg.ngstars.report.dto.CreateEmailTemplateRequest;
import tg.ngstars.report.dto.EmailTemplateResponse;
import tg.ngstars.report.dto.UpdateEmailTemplateRequest;
import tg.ngstars.report.service.EmailTemplateService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports/email-templates")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class EmailTemplateController {

    private final EmailTemplateService service;

    public EmailTemplateController(EmailTemplateService service) {
        this.service = service;
    }

    @GetMapping
    public ResponseEntity<List<EmailTemplateResponse>> list() {
        return ResponseEntity.ok(service.listAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmailTemplateResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping("/key/{key}")
    public ResponseEntity<EmailTemplateResponse> getByKey(@PathVariable String key) {
        return ResponseEntity.ok(service.getByKey(key));
    }

    @PostMapping
    public ResponseEntity<EmailTemplateResponse> create(
            @Valid @RequestBody CreateEmailTemplateRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(service.create(request, jwt.getSubject()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<EmailTemplateResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateEmailTemplateRequest request) {
        return ResponseEntity.ok(service.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
