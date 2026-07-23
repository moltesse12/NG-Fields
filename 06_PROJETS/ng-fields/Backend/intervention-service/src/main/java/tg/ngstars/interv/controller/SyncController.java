package tg.ngstars.interv.controller;

import java.util.ArrayList;
import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import tg.ngstars.interv.dto.SyncBatchRequest;
import tg.ngstars.interv.dto.SyncBatchResponse;
import tg.ngstars.interv.dto.SyncRequest;
import tg.ngstars.interv.dto.SyncResponse;
import tg.ngstars.interv.service.InterventionService;
import tg.ngstars.interv.service.SecurityUtils;

@RestController
@RequestMapping("/api/sync")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
@Tag(name = "Sync", description = "Synchronisation offline des interventions mobile")
public class SyncController {

    private final InterventionService interventionService;
    private final SecurityUtils securityUtils;

    public SyncController(InterventionService interventionService, SecurityUtils securityUtils) {
        this.interventionService = interventionService;
        this.securityUtils = securityUtils;
    }

    @PostMapping("/interventions")
    @Operation(summary = "Synchroniser une seule intervention depuis le mobile", description = "Idempotent. Gere les conflits last-write-wins.")
    @ApiResponse(responseCode = "200", description = "Sync reussie")
    public ResponseEntity<SyncResponse> syncIntervention(@Valid @RequestBody SyncRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.syncFromMobile(request, userId, securityUtils.isAdminOrManager()));
    }

    @PostMapping("/batch")
    @Operation(summary = "Synchroniser un lot d'interventions (max 10)", description = "Batch offline : chaque item est traite individuellement, les erreurs partielles ne bloquent pas le lot.")
    @ApiResponse(responseCode = "200", description = "Batch traite avec resultats individuels")
    @ApiResponse(responseCode = "400", description = "Lot invalide ou vide")
    public ResponseEntity<SyncBatchResponse> syncBatch(
            @Valid @RequestBody @Parameter(description = "Liste de 1 a 10 interventions a synchroniser") SyncBatchRequest request) {
        var userId = securityUtils.getCurrentUserId();
        var isAdmin = securityUtils.isAdminOrManager();
        var start = System.currentTimeMillis();
        List<SyncResponse> results = new ArrayList<>();
        for (SyncRequest item : request.interventions()) {
            try {
                results.add(interventionService.syncFromMobile(item, userId, isAdmin));
            } catch (Exception e) {
                results.add(SyncResponse.error("Erreur sur localId=" + item.localId() + ": " + e.getMessage()));
            }
        }
        return ResponseEntity.ok(SyncBatchResponse.of(results, System.currentTimeMillis() - start));
    }
}
