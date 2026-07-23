package tg.ngstars.interv.controller;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import tg.ngstars.interv.dto.CreateInterventionRequest;
import tg.ngstars.interv.dto.InterventionResponse;
import tg.ngstars.interv.dto.WeeklyScheduleDTO;
import tg.ngstars.interv.model.Intervention;
import tg.ngstars.interv.repository.InterventionRepository;
import tg.ngstars.interv.service.InterventionService;
import tg.ngstars.interv.service.SecurityUtils;

@RestController
@RequestMapping("/api/manager")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
@Tag(name = "Manager Planning", description = "Gestion planning, assignation et interventions planifiees")
public class ManagerController {

    private final InterventionService interventionService;
    private final InterventionRepository interventionRepository;
    private final SecurityUtils securityUtils;

    public ManagerController(InterventionService interventionService,
                             InterventionRepository interventionRepository,
                             SecurityUtils securityUtils) {
        this.interventionService = interventionService;
        this.interventionRepository = interventionRepository;
        this.securityUtils = securityUtils;
    }

    @PostMapping("/interventions")
    @Operation(summary = "Creer une intervention planifiee depuis le manager", description = "Reserve une intervention pour un technicien a une date donnee.")
    @ApiResponse(responseCode = "201", description = "Intervention creee")
    public ResponseEntity<InterventionResponse> createPlannedIntervention(
            @Valid @RequestBody CreateInterventionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(interventionService.createIntervention(request, securityUtils.getCurrentUserId()));
    }

    @PutMapping("/interventions/{id}/assign")
    @Operation(summary = "Assigner / reassigner un technicien a une intervention", description = "Change l'assignedTo et passe le statut a ASSIGNED.")
    @ApiResponse(responseCode = "200", description = "Intervention reassignee")
    public ResponseEntity<InterventionResponse> assignIntervention(
            @PathVariable UUID id,
            @RequestBody java.util.Map<String, UUID> body) {
        var userId = securityUtils.getCurrentUserId();
        var assignedTo = body.get("assignedTo");
        if (assignedTo == null) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(interventionService.assignIntervention(id, assignedTo, userId, true));
    }

    @GetMapping("/technicians/{technicianId}/schedule")
    @Operation(summary = "Planning hebdomadaire d'un technicien", description = "Retourne les interventions d'un technicien pour une semaine donnee (lundi-dimanche).")
    @ApiResponse(responseCode = "200", description = "Planning retourne")
    public ResponseEntity<WeeklyScheduleDTO> getWeeklySchedule(
            @PathVariable UUID technicianId,
            @RequestParam(required = false) LocalDate weekStart) {
        if (weekStart == null) {
            weekStart = LocalDate.now().with(DayOfWeek.MONDAY);
        }
        var weekEnd = weekStart.plusDays(6);
        var startDt = weekStart.atStartOfDay().atOffset(ZoneOffset.UTC);
        var endDt = weekEnd.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);

        var interventions = interventionRepository.findByActiveTrueAndAssignedToAndInterventionDateBetweenOrderByInterventionDateAsc(
                technicianId, startDt, endDt);

        var days = weekStart.datesUntil(weekEnd.plusDays(1)).map(day -> {
            var dayStart = day.atStartOfDay().atOffset(ZoneOffset.UTC);
            var dayEnd = day.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC);
            var dayInterventions = interventions.stream()
                    .filter(i -> i.getInterventionDate() != null
                            && !i.getInterventionDate().isBefore(dayStart)
                            && i.getInterventionDate().isBefore(dayEnd))
                    .map(i -> new WeeklyScheduleDTO.ScheduleSlotDTO(
                            i.getId(), i.getReference(), i.getClientName(),
                            i.getStatus(), i.getSiteAddress(), i.getSiteCity(),
                            i.getStartTime(), i.getEndTime(), i.getDurationMinutes()))
                    .toList();
            return new WeeklyScheduleDTO.ScheduleDayDTO(dayStart, dayInterventions);
        }).toList();

        return ResponseEntity.ok(new WeeklyScheduleDTO(technicianId, null, days));
    }

    @GetMapping("/interventions")
    @Operation(summary = "Lister toutes les interventions (vue manager)", description = "Filtrable par statut, date, technicien.")
    @ApiResponse(responseCode = "200", description = "Liste retournee")
    public ResponseEntity<List<InterventionResponse>> listAllInterventions(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID technicianId) {
        var list = interventionRepository.findByActiveTrueOrderByCreatedAtDesc(
                org.springframework.data.domain.PageRequest.of(0, 100));
        var filtered = list.stream().filter(i -> {
            if (status != null && !i.getStatus().equals(status)) return false;
            if (technicianId != null && !technicianId.equals(i.getAssignedTo())) return false;
            return true;
        }).toList();
        return ResponseEntity.ok(filtered.stream().map(i -> {
            var items = i.getItems() != null ? i.getItems().stream()
                    .<InterventionResponse.ItemResponse>map(item -> new InterventionResponse.ItemResponse(
                            item.getId(), item.getType(), item.getDescription(),
                            item.getQuantity(), item.getUnitPrice(), item.getTotal(),
                            item.getCreatedAt())).toList() : List.<InterventionResponse.ItemResponse>of();
            return new InterventionResponse(
                    i.getId(), i.getReference(), i.getClientId(),
                    i.getClientName(), i.getClientEmail(), i.getClientPhone(),
                    i.getClientAddress(), i.getEquipmentType(), i.getEquipmentBrand(),
                    i.getEquipmentModel(), i.getEquipmentSerial(),
                    i.getEquipmentLocation(), i.getReportedIssue(),
                    i.getDiagnosis(), i.getWorkDone(), i.getStatus(), i.getInterventionDate(),
                    i.getCreatedBy(), i.getAssignedTo(), i.getSiteAddress(),
                    i.getSiteCity(), i.getEstimatedCost(), i.getGpsLatitude(), i.getGpsLongitude(), i.getTotalCost(),
                    i.getClientSignature(), i.getTechnicianSignature(), i.getManagerSignature(), i.getSignedAt(),
                    i.getDepartureTime(), i.getArrivalTime(), i.getStartTime(), i.getEndTime(), i.getDurationMinutes(),
                    i.getResult(), i.getFollowUpRecommended(), i.getRecommendations(),
                    i.getLocalId(), i.getNotes(), i.getActive(), i.getCreatedAt(), i.getUpdatedAt(), items);
        }).toList());
    }
}
