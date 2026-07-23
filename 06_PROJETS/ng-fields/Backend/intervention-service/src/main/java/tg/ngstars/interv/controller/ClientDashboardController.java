package tg.ngstars.interv.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import tg.ngstars.interv.dto.DashboardStatsDTO;
import tg.ngstars.interv.service.DashboardService;
import tg.ngstars.interv.service.SecurityUtils;

@RestController
@RequestMapping("/api/client/dashboard")
@PreAuthorize("hasAnyRole('CLIENT_ADMIN', 'CLIENT_USER', 'CLIENT_VIEWER')")
@Tag(name = "Client Dashboard", description = "KPIs et statistiques filtres par entreprise")
public class ClientDashboardController {

    private final DashboardService dashboardService;
    private final SecurityUtils securityUtils;

    public ClientDashboardController(DashboardService dashboardService, SecurityUtils securityUtils) {
        this.dashboardService = dashboardService;
        this.securityUtils = securityUtils;
    }

    @GetMapping
    @Operation(summary = "Dashboard de l'entreprise", description = "KPIs filtres par company_id du JWT. Total interventions, par statut, couts, duree moyenne, nombre techniciens affectes.")
    @ApiResponse(responseCode = "200", description = "Dashboard retourne")
    @ApiResponse(responseCode = "400", description = "company_id manquant dans le token")
    public ResponseEntity<DashboardStatsDTO> getClientDashboard() {
        var companyId = securityUtils.getCompanyId();
        if (companyId == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(dashboardService.getClientDashboard(companyId));
    }
}
