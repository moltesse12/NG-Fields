package tg.ngstars.interv.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tg.ngstars.interv.dto.SyncRequest;
import tg.ngstars.interv.dto.SyncResponse;
import tg.ngstars.interv.service.InterventionService;
import tg.ngstars.interv.service.SecurityUtils;

@RestController
@RequestMapping("/api/sync")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
public class SyncController {

    private final InterventionService interventionService;
    private final SecurityUtils securityUtils;

    public SyncController(InterventionService interventionService, SecurityUtils securityUtils) {
        this.interventionService = interventionService;
        this.securityUtils = securityUtils;
    }

    @PostMapping("/interventions")
    public ResponseEntity<SyncResponse> syncIntervention(@Valid @RequestBody SyncRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.syncFromMobile(request, userId, securityUtils.isAdminOrManager()));
    }
}
