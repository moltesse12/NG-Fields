package tg.ngstars.interv.controller;

import java.util.UUID;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import tg.ngstars.interv.repository.InterventionRepository;
import tg.ngstars.interv.service.ExportService;

@RestController
@RequestMapping("/api/manager/export")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
@Tag(name = "Manager Export", description = "Export des interventions en CSV ou HTML")
public class ExportController {

    private final InterventionRepository interventionRepository;
    private final ExportService exportService;

    public ExportController(InterventionRepository interventionRepository, ExportService exportService) {
        this.interventionRepository = interventionRepository;
        this.exportService = exportService;
    }

    @GetMapping
    @Operation(summary = "Exporter les interventions", description = "Export CSV ou HTML de toutes les interventions actives.")
    @ApiResponse(responseCode = "200", description = "Fichier genere")
    public ResponseEntity<byte[]> export(
            @RequestParam(defaultValue = "csv") @Parameter(description = "Format : csv ou html") String format,
            @RequestParam(required = false) @Parameter(description = "Filtrer par statut") String status,
            @RequestParam(required = false) @Parameter(description = "Filtrer par technicien") UUID technicianId) throws java.io.IOException {

        var interventions = (technicianId != null && status != null)
                ? interventionRepository.findFirst100ByActiveTrueAndAssignedToAndStatusOrderByCreatedAtDesc(technicianId, status)
                : (technicianId != null
                        ? interventionRepository.findFirst100ByActiveTrueAndAssignedToOrderByCreatedAtDesc(technicianId)
                        : (status != null
                                ? interventionRepository.findFirst100ByActiveTrueAndStatusOrderByCreatedAtDesc(status)
                                : interventionRepository.findFirst100ByActiveTrueOrderByCreatedAtDesc()));

        if (format.equalsIgnoreCase("html")) {
            var html = exportService.exportInterventionsHtml(interventions);
            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=interventions.html")
                    .contentType(MediaType.TEXT_HTML)
                    .body(html);
        }

        var csv = exportService.exportInterventionsCsv(interventions);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=interventions.csv")
                .contentType(MediaType.parseMediaType("text/csv;charset=UTF-8"))
                .body(csv);
    }
}
