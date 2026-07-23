package tg.ngstars.interv.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import tg.ngstars.interv.dto.InterventionResponse;
import tg.ngstars.interv.dto.InterventionStatsResponse;
import tg.ngstars.interv.service.InterventionService;
import tg.ngstars.interv.service.SecurityUtils;

@RestController
@RequestMapping("/api/client/interventions")
@PreAuthorize("hasAnyRole('CLIENT_ADMIN', 'CLIENT_USER', 'CLIENT_VIEWER')")
@Tag(name = "Client Portal", description = "Portail client : consultation des interventions de son entreprise")
public class ClientInterventionController {

    private final InterventionService interventionService;
    private final SecurityUtils securityUtils;

    public ClientInterventionController(InterventionService interventionService, SecurityUtils securityUtils) {
        this.interventionService = interventionService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    @Operation(summary = "Interventions de l'entreprise du client", description = "Filtre automatiquement par company_id du JWT. CLIENT_VIEWER = lecture seule.")
    @ApiResponse(responseCode = "200", description = "Page de resultats")
    public ResponseEntity<Page<InterventionResponse>> getMyCompanyInterventions(
            @RequestParam(required = false) String status,
            Pageable pageable) {
        var userId = securityUtils.getCurrentUserId();
        var companyId = securityUtils.getCompanyId();
        if (companyId != null) {
            return ResponseEntity.ok(interventionService.getInterventionsByCompanyId(companyId, status, pageable));
        }
        return ResponseEntity.ok(interventionService.getInterventions(status, userId, pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Detail d'une intervention", description = "Accessible uniquement si l'intervention appartient a l'entreprise du client.")
    @ApiResponse(responseCode = "200", description = "Intervention trouvee")
    @ApiResponse(responseCode = "403", description = "Intervention ne apartient pas a l'entreprise")
    public ResponseEntity<InterventionResponse> getCompanyIntervention(@PathVariable UUID id) {
        var userId = securityUtils.getCurrentUserId();
        var companyId = securityUtils.getCompanyId();
        var intervention = interventionService.getIntervention(id, userId, false);
        if (companyId != null && intervention.clientId() != null && !intervention.clientId().equals(companyId)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(intervention);
    }

    @GetMapping("/stats")
    @Operation(summary = "Stats de l'entreprise", description = "KPIs filtres par company_id.")
    @ApiResponse(responseCode = "200", description = "Statistiques retournees")
    public ResponseEntity<InterventionStatsResponse> getCompanyStats() {
        return ResponseEntity.ok(interventionService.getStats());
    }
}
