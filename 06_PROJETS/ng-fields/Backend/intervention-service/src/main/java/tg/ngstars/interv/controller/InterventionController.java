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

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import tg.ngstars.interv.dto.ClientDataSyncRequest;
import tg.ngstars.interv.dto.CreateInterventionRequest;
import tg.ngstars.interv.dto.InterventionResponse;
import tg.ngstars.interv.dto.InterventionStatsResponse;
import tg.ngstars.interv.dto.ItemRequest;
import tg.ngstars.interv.dto.SendEmailRequest;
import tg.ngstars.interv.dto.UpdateDiagnosisRequest;
import tg.ngstars.interv.dto.UpdateEquipmentRequest;
import tg.ngstars.interv.dto.UpdateInterventionRequest;
import tg.ngstars.interv.dto.UpdateRecommendationsRequest;
import tg.ngstars.interv.dto.UpdateResultRequest;
import tg.ngstars.interv.dto.UpdateScheduleRequest;
import tg.ngstars.interv.service.InterventionService;
import tg.ngstars.interv.service.SecurityUtils;

@RestController
@RequestMapping("/api/interventions")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN', 'CLIENT_ADMIN', 'CLIENT_USER', 'CLIENT_VIEWER')")
@Tag(name = "Interventions", description = "CRUD complet des interventions")
public class InterventionController {

    private final InterventionService interventionService;
    private final SecurityUtils securityUtils;

    public InterventionController(InterventionService interventionService, SecurityUtils securityUtils) {
        this.interventionService = interventionService;
        this.securityUtils = securityUtils;
    }

    @PostMapping
    @Operation(summary = "Creer une intervention", description = "Cree une nouvelle fiche d'intervention. La reference doit etre unique.")
    @ApiResponse(responseCode = "201", description = "Intervention creee")
    @ApiResponse(responseCode = "409", description = "Reference deja existante")
    public ResponseEntity<InterventionResponse> createIntervention(
            @Valid @RequestBody CreateInterventionRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(interventionService.createIntervention(request, securityUtils.getCurrentUserId()));
    }

    @GetMapping
    @Operation(summary = "Lister les interventions", description = "Pagine. Techniciens ne voient que leurs interventions. Les managers/admins voient tout.")
    @ApiResponse(responseCode = "200", description = "Page de resultats")
    public ResponseEntity<Page<InterventionResponse>> getInterventions(
            @RequestParam(required = false) @Parameter(description = "Filtrer par statut") String status,
            @RequestParam(required = false) @Parameter(description = "Filtrer par technicien (admin/manager uniquement)") UUID technicianId,
            Pageable pageable) {
        var currentUserId = securityUtils.getCurrentUserId();
        var isAdminOrManager = securityUtils.isAdminOrManager();
        var techId = isAdminOrManager ? technicianId : currentUserId;
        return ResponseEntity.ok(interventionService.getInterventions(status, techId, pageable));
    }

    @GetMapping("/stats")
    @Operation(summary = "Statistiques globales", description = "KPIs : nombre par statut, duree moyenne, cout total estime.")
    @ApiResponse(responseCode = "200", description = "Statistiques retournees")
    public ResponseEntity<InterventionStatsResponse> getStats() {
        return ResponseEntity.ok(interventionService.getStats());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtenir une intervention", description = "Inclut les items. Techniciens ne voient que leurs propres interventions.")
    @ApiResponse(responseCode = "200", description = "Intervention trouvee")
    @ApiResponse(responseCode = "404", description = "Intervention introuvable")
    public ResponseEntity<InterventionResponse> getIntervention(@PathVariable UUID id) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.getIntervention(id, userId, securityUtils.isAdminOrManager()));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Mettre a jour une intervention", description = "Mise a jour complete. Recalcul automatique du totalCost si items modifies.")
    @ApiResponse(responseCode = "200", description = "Intervention mise a jour")
    public ResponseEntity<InterventionResponse> updateIntervention(@PathVariable UUID id,
            @Valid @RequestBody CreateInterventionRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.updateIntervention(id, request, userId, securityUtils.isAdminOrManager()));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Supprimer (soft delete)", description = "Desactive l'intervention. Les signatures requises pour cloturer.")
    @ApiResponse(responseCode = "204", description = "Intervention desactivee")
    public ResponseEntity<Void> deleteIntervention(@PathVariable UUID id) {
        var userId = securityUtils.getCurrentUserId();
        interventionService.deleteIntervention(id, userId, securityUtils.isAdminOrManager());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/pdf")
    @Operation(summary = "Generer le PDF d'une intervention", description = "PDF avec tous les details, items, signatures.")
    @ApiResponse(responseCode = "200", description = "PDF genere")
    public ResponseEntity<StreamingResponseBody> generatePdf(@PathVariable UUID id) {
        var userId = securityUtils.getCurrentUserId();
        StreamingResponseBody stream = out -> interventionService.generatePdfToStream(id, userId, securityUtils.isAdminOrManager(), out);
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=intervention.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(stream);
    }

    @GetMapping("/by-client/{clientId}")
    @Operation(summary = "Interventions d'un client", description = "Liste les interventions liees a un client specifique.")
    @ApiResponse(responseCode = "200", description = "Page de resultats")
    public ResponseEntity<Page<InterventionResponse>> getClientInterventions(
            @PathVariable UUID clientId, Pageable pageable) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.getClientInterventions(clientId, userId, securityUtils.isAdminOrManager(), pageable));
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Mettre a jour GPS", description = "Latitude et longitude du site d'intervention.")
    @ApiResponse(responseCode = "200", description = "GPS mis a jour")
    public ResponseEntity<InterventionResponse> updateInterventionGps(@PathVariable UUID id,
            @Valid @RequestBody UpdateInterventionRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.updateInterventionGps(
                id, request, userId, securityUtils.isAdminOrManager()));
    }

    @PatchMapping("/{id}/schedule")
    @Operation(summary = "Mettre a jour les horaires", description = "Depart, arrivee, debut, fin. Calcul automatique de durationMinutes.")
    @ApiResponse(responseCode = "200", description = "Horaires mis a jour")
    public ResponseEntity<InterventionResponse> updateSchedule(@PathVariable UUID id,
            @Valid @RequestBody UpdateScheduleRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.updateSchedule(id, request, userId, securityUtils.isAdminOrManager()));
    }

    @PatchMapping("/{id}/equipment")
    @Operation(summary = "Mettre a jour l'equipement", description = "Marque, modele, numero de serie, localisation.")
    @ApiResponse(responseCode = "200", description = "Equipement mis a jour")
    public ResponseEntity<InterventionResponse> updateEquipment(@PathVariable UUID id,
            @Valid @RequestBody UpdateEquipmentRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.updateEquipment(id, request, userId, securityUtils.isAdminOrManager()));
    }

    @PatchMapping("/{id}/diagnosis")
    @Operation(summary = "Mettre a jour le diagnostic", description = "Diagnostic et travaux realises.")
    @ApiResponse(responseCode = "200", description = "Diagnostic mis a jour")
    public ResponseEntity<InterventionResponse> updateDiagnosis(@PathVariable UUID id,
            @Valid @RequestBody UpdateDiagnosisRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.updateDiagnosis(id, request, userId, securityUtils.isAdminOrManager()));
    }

    @PatchMapping("/{id}/result")
    @Operation(summary = "Mettre a jour le resultat", description = "RESOLVED ou UNRESOLVED. Si UNRESOLVED, followUpRecommended passe a true automatiquement.")
    @ApiResponse(responseCode = "200", description = "Resultat mis a jour")
    public ResponseEntity<InterventionResponse> updateResult(@PathVariable UUID id,
            @Valid @RequestBody UpdateResultRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.updateResult(id, request, userId, securityUtils.isAdminOrManager()));
    }

    @PatchMapping("/{id}/recommendations")
    @Operation(summary = "Mettre a jour les recommandations", description = "Texte libre de recommandations post-intervention.")
    @ApiResponse(responseCode = "200", description = "Recommandations mises a jour")
    public ResponseEntity<InterventionResponse> updateRecommendations(@PathVariable UUID id,
            @Valid @RequestBody UpdateRecommendationsRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.updateRecommendations(id, request, userId, securityUtils.isAdminOrManager()));
    }

    @PostMapping("/{id}/items")
    @Operation(summary = "Ajouter un item", description = "Piece ou main d'oeuvre. Recalcul du totalCost automatique.")
    @ApiResponse(responseCode = "201", description = "Item ajoute")
    public ResponseEntity<InterventionResponse> addItem(@PathVariable UUID id,
            @Valid @RequestBody ItemRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(interventionService.addItem(id, request, userId, securityUtils.isAdminOrManager()));
    }

    @PutMapping("/{id}/items/{itemId}")
    @Operation(summary = "Modifier un item", description = "Quantite, prix unitaire, description. Recalcul du totalCost.")
    @ApiResponse(responseCode = "200", description = "Item modifie")
    public ResponseEntity<InterventionResponse> updateItem(@PathVariable UUID id, @PathVariable UUID itemId,
            @Valid @RequestBody ItemRequest request) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.updateItem(id, itemId, request, userId, securityUtils.isAdminOrManager()));
    }

    @DeleteMapping("/{id}/items/{itemId}")
    @Operation(summary = "Supprimer un item", description = "Supprime l'item et recalcule le totalCost.")
    @ApiResponse(responseCode = "200", description = "Item supprime")
    public ResponseEntity<InterventionResponse> removeItem(@PathVariable UUID id, @PathVariable UUID itemId) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.removeItem(id, itemId, userId, securityUtils.isAdminOrManager()));
    }

    @PostMapping("/{id}/close")
    @Operation(summary = "Cloturer une intervention", description = "Toutes les signatures (client, tech, manager) sont requises. Statut -> COMPLETED.")
    @ApiResponse(responseCode = "200", description = "Intervention cloturee")
    @ApiResponse(responseCode = "409", description = "Signatures manquantes ou transition invalide")
    public ResponseEntity<InterventionResponse> closeIntervention(@PathVariable UUID id) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.closeIntervention(id, userId, securityUtils.isAdminOrManager()));
    }

    @PostMapping("/{id}/assign")
    @Operation(summary = "Assigner un technicien", description = "Change l'assignedTo et passe le statut a ASSIGNED.")
    @ApiResponse(responseCode = "200", description = "Technicien assigne")
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
    @Operation(summary = "Demarrer une intervention", description = "Statut -> IN_PROGRESS. Declenche le chrono.")
    @ApiResponse(responseCode = "200", description = "Intervention demarree")
    public ResponseEntity<InterventionResponse> startIntervention(@PathVariable UUID id) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.startIntervention(id, userId, securityUtils.isAdminOrManager()));
    }

    @PostMapping("/{id}/cancel")
    @Operation(summary = "Annuler une intervention", description = "Statut -> CANCELLED. Admin ou manager uniquement.")
    @ApiResponse(responseCode = "200", description = "Intervention annulee")
    @ApiResponse(responseCode = "403", description = "Non autorise")
    public ResponseEntity<InterventionResponse> cancelIntervention(@PathVariable UUID id) {
        var userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(interventionService.cancelIntervention(id, userId, securityUtils.isAdminOrManager()));
    }

    @PostMapping("/{id}/send-email")
    @Operation(summary = "Envoyer le rapport par email", description = "Genere le PDF et l'envoie via Resend a l'adresse specifiee.")
    @ApiResponse(responseCode = "200", description = "Email envoye")
    public ResponseEntity<java.util.Map<String, String>> sendEmail(@PathVariable UUID id,
            @Valid @RequestBody SendEmailRequest request) {
        var userId = securityUtils.getCurrentUserId();
        interventionService.sendEmailReport(id, request.email(), userId, securityUtils.isAdminOrManager());
        return ResponseEntity.ok(java.util.Map.of("message", "Email sent to " + request.email()));
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
