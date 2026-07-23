package tg.ngstars.report.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tg.ngstars.report.dto.CreatePdfTemplateRequest;
import tg.ngstars.report.dto.PdfTemplateResponse;
import tg.ngstars.report.dto.UpdatePdfTemplateRequest;
import tg.ngstars.report.service.PdfTemplateService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/reports/templates")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class PdfTemplateController {

    private final PdfTemplateService templateService;

    public PdfTemplateController(PdfTemplateService templateService) {
        this.templateService = templateService;
    }

    @GetMapping
    public ResponseEntity<List<PdfTemplateResponse>> list(
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(templateService.listAll(type));
    }

    @GetMapping("/{id}")
    public ResponseEntity<PdfTemplateResponse> getById(@PathVariable UUID id) {
        return ResponseEntity.ok(templateService.getById(id));
    }

    @GetMapping("/default")
    public ResponseEntity<PdfTemplateResponse> getDefault(
            @RequestParam(required = false) String type) {
        return ResponseEntity.ok(templateService.getDefault(type));
    }

    @PostMapping
    public ResponseEntity<PdfTemplateResponse> create(
            @Valid @RequestBody CreatePdfTemplateRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        var userId = jwt.getSubject();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(templateService.create(request, userId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PdfTemplateResponse> update(
            @PathVariable UUID id,
            @Valid @RequestBody UpdatePdfTemplateRequest request) {
        return ResponseEntity.ok(templateService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        templateService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
