package tg.ngstars.interv.controller;

import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import tg.ngstars.interv.dto.ClientDataSyncRequest;
import tg.ngstars.interv.dto.CreateInterventionRequest;
import tg.ngstars.interv.dto.InterventionResponse;
import tg.ngstars.interv.dto.ItemRequest;
import tg.ngstars.interv.dto.UpdateBillingRequest;
import tg.ngstars.interv.dto.UpdateDiagnosisRequest;
import tg.ngstars.interv.dto.UpdateEquipmentRequest;
import tg.ngstars.interv.dto.UpdateRecommendationsRequest;
import tg.ngstars.interv.dto.UpdateResultRequest;
import tg.ngstars.interv.dto.UpdateScheduleRequest;
import tg.ngstars.interv.service.InterventionService;
import tg.ngstars.interv.service.SecurityUtils;

@RestController
@RequestMapping("/api/interventions")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
public class InterventionController {

    private final InterventionService interventionService;
    private final SecurityUtils securityUtils;

    public InterventionController(InterventionService interventionService, SecurityUtils securityUtils) {
        this.interventionService = interventionService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    public ResponseEntity<InterventionResponse> createIntervention(
            @Valid @RequestBody CreateInterventionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(interventionService.createIntervention(request, securityUtils.getCurrentUserId()));
    }

    @GetMapping
    public ResponseEntity<Page<InterventionResponse>> getInterventions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID technicianId,
            Pageable pageable) {
        var currentUserId = securityUtils.getCurrentUserId();
        var isAdminOrManager = securityUtils.isAdminOrManager();
        var techId = isAdminOrManager ? technicianId : currentUserId;
        return ResponseEntity.ok(interventionService.getInterventions(status, techId, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterventionResponse> getIntervention(@PathVariable UUID id) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.getIntervention(id, userId, securityUtils.isAdminOrManager()));
    }

    @PutMapping("/{id}")
    public ResponseEntity<InterventionResponse> updateIntervention(@PathVariable UUID id,
            @Valid @RequestBody CreateInterventionRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.updateIntervention(id, request, userId, securityUtils.isAdminOrManager()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteIntervention(@PathVariable UUID id) {
        var userId = securityUtils.getCurrentUserId();
        interventionService.deleteIntervention(id, userId, securityUtils.isAdminOrManager());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pdf")
    public ResponseEntity<StreamingResponseBody> generatePdf(@PathVariable UUID id) {
        var userId = securityUtils.getCurrentUserId();
        StreamingResponseBody stream = out -> interventionService.generatePdfToStream(id, userId, securityUtils.isAdminOrManager(), out);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=intervention.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(stream);
    }

    @GetMapping("/by-client/{clientId}")
    public ResponseEntity<Page<InterventionResponse>> getClientInterventions(
            @PathVariable UUID clientId, Pageable pageable) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.getClientInterventions(clientId, userId, securityUtils.isAdminOrManager(), pageable));
    }

    @PatchMapping("/{id}/schedule")
    public ResponseEntity<InterventionResponse> updateSchedule(@PathVariable UUID id,
            @Valid @RequestBody UpdateScheduleRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.updateSchedule(id, request, userId, securityUtils.isAdminOrManager()));
    }

    @PatchMapping("/{id}/equipment")
    public ResponseEntity<InterventionResponse> updateEquipment(@PathVariable UUID id,
            @Valid @RequestBody UpdateEquipmentRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.updateEquipment(id, request, userId, securityUtils.isAdminOrManager()));
    }

    @PatchMapping("/{id}/diagnosis")
    public ResponseEntity<InterventionResponse> updateDiagnosis(@PathVariable UUID id,
            @Valid @RequestBody UpdateDiagnosisRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.updateDiagnosis(id, request, userId, securityUtils.isAdminOrManager()));
    }

    @PatchMapping("/{id}/result")
    public ResponseEntity<InterventionResponse> updateResult(@PathVariable UUID id,
            @Valid @RequestBody UpdateResultRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.updateResult(id, request, userId, securityUtils.isAdminOrManager()));
    }

    @PatchMapping("/{id}/recommendations")
    public ResponseEntity<InterventionResponse> updateRecommendations(@PathVariable UUID id,
            @Valid @RequestBody UpdateRecommendationsRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.updateRecommendations(id, request, userId, securityUtils.isAdminOrManager()));
    }

    @PatchMapping("/{id}/billing")
    public ResponseEntity<InterventionResponse> updateBilling(@PathVariable UUID id,
            @Valid @RequestBody UpdateBillingRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.updateBilling(id, request, userId, securityUtils.isAdminOrManager()));
    }

    @PostMapping("/{id}/items")
    public ResponseEntity<InterventionResponse> addItem(@PathVariable UUID id,
            @Valid @RequestBody ItemRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(interventionService.addItem(id, request, userId, securityUtils.isAdminOrManager()));
    }

    @PutMapping("/{id}/items/{itemId}")
    public ResponseEntity<InterventionResponse> updateItem(@PathVariable UUID id, @PathVariable UUID itemId,
            @Valid @RequestBody ItemRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.updateItem(id, itemId, request, userId, securityUtils.isAdminOrManager()));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    public ResponseEntity<InterventionResponse> removeItem(@PathVariable UUID id, @PathVariable UUID itemId) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.removeItem(id, itemId, userId, securityUtils.isAdminOrManager()));
    }

    @PostMapping("/{id}/close")
    public ResponseEntity<InterventionResponse> closeIntervention(@PathVariable UUID id) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.closeIntervention(id, userId, securityUtils.isAdminOrManager()));
    }

    @PostMapping("/{id}/assign")
    public ResponseEntity<InterventionResponse> assignIntervention(@PathVariable UUID id,
            @RequestBody java.util.Map<String, java.util.UUID> body) {
        var userId = securityUtils.getCurrentUserId();
        var assignedTo = body.get("assignedTo");
        if (assignedTo == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(interventionService.assignIntervention(id, assignedTo, userId, securityUtils.isAdminOrManager()));
    }

    @PostMapping("/{id}/start")
    public ResponseEntity<InterventionResponse> startIntervention(@PathVariable UUID id) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.startIntervention(id, userId, securityUtils.isAdminOrManager()));
    }

    @PostMapping("/{id}/cancel")
    public ResponseEntity<InterventionResponse> cancelIntervention(@PathVariable UUID id) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.cancelIntervention(id, userId, securityUtils.isAdminOrManager()));
    }

    @PostMapping("/sync/client-data")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<java.util.Map<String, Object>> syncClientData(
            @Valid @RequestBody ClientDataSyncRequest request) {
        int updated = interventionService.syncClientData(
                request.clientId(), request.clientName(),
                request.clientEmail(), request.clientPhone(), request.clientAddress());
        return ResponseEntity.ok(java.util.Map.of("updated", updated));
    }
}
