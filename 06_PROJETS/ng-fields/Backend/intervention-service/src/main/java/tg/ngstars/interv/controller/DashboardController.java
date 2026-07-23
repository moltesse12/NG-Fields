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
@RequestMapping("/api/manager/dashboard")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
@Tag(name = "Manager Dashboard", description = "KPIs et statistiques globales pour les managers")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping
    @Operation(summary = "Dashboard manager global", description = "KPIs globaux : total interventions, par statut, couts, nombre clients, nombre techniciens, duree moyenne.")
    @ApiResponse(responseCode = "200", description = "Dashboard retourne")
    public ResponseEntity<DashboardStatsDTO> getDashboard() {
        return ResponseEntity.ok(dashboardService.getManagerDashboard());
    }
}
