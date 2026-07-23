package tg.ngstars.interv.service;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import tg.ngstars.interv.dto.CreateInterventionRequest;
import tg.ngstars.interv.dto.CreateInterventionRequest.CreateItemRequest;
import tg.ngstars.interv.dto.InterventionEvent;
import tg.ngstars.interv.dto.InterventionResponse;
import tg.ngstars.interv.dto.InterventionResponse.ItemResponse;
import tg.ngstars.interv.dto.InterventionStatsResponse;
import tg.ngstars.interv.dto.ItemRequest;
import tg.ngstars.interv.dto.SyncRequest;
import tg.ngstars.interv.dto.SyncResponse;
import tg.ngstars.interv.dto.UpdateDiagnosisRequest;
import tg.ngstars.interv.dto.UpdateEquipmentRequest;
import tg.ngstars.interv.dto.UpdateInterventionRequest;
import tg.ngstars.interv.dto.UpdateRecommendationsRequest;
import tg.ngstars.interv.dto.UpdateResultRequest;
import tg.ngstars.interv.dto.UpdateScheduleRequest;
import tg.ngstars.common.exception.ForbiddenException;
import tg.ngstars.common.exception.NotFoundException;
import tg.ngstars.interv.model.Intervention;
import tg.ngstars.interv.model.InterventionItem;
import tg.ngstars.interv.repository.InterventionRepository;

@Service
@Transactional(readOnly = true)
public class InterventionService {

    private static final Logger log = LoggerFactory.getLogger(InterventionService.class);

    private final InterventionRepository interventionRepository;
    private final InterventionStatusService statusService;
    private final InterventionEmailService emailService;
    private final SseEmitterManager sseManager;

    public InterventionService(InterventionRepository interventionRepository, InterventionStatusService statusService, InterventionEmailService emailService, SseEmitterManager sseManager) {
        this.interventionRepository = interventionRepository;
        this.statusService = statusService;
        this.emailService = emailService;
        this.sseManager = sseManager;
    }

    @Transactional
    public int syncClientData(UUID clientId, String name, String email, String phone, String address) {
        int updated = interventionRepository.syncClientData(clientId, name, email, phone, address);
        if (updated > 0) {
            log.info("Données client synchronisées pour {} interventions (client={})", updated, clientId);
        }
        return updated;
    }

    private Intervention findOrThrow(UUID id) {
        return interventionRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Intervention not found: " + id));
    }

    private void checkOwnership(Intervention intervention, UUID userId, boolean isAdminOrManager) {
        if (isAdminOrManager) return;
        if (intervention.getAssignedTo() == null || !intervention.getAssignedTo().equals(userId))
            throw new ForbiddenException("Not assigned to this intervention");
    }

    @Transactional
    public InterventionResponse createIntervention(CreateInterventionRequest request, UUID userId) {
        if (interventionRepository.existsByReference(request.reference()))
            throw new IllegalArgumentException("Reference already exists: " + request.reference());

        var intervention = Intervention.builder()
                .reference(request.reference())
                .clientId(request.clientId())
                .clientName(request.clientName())
                .clientEmail(request.clientEmail())
                .clientPhone(request.clientPhone())
                .clientAddress(request.clientAddress())
                .equipmentType(request.equipmentType())
                .equipmentBrand(request.equipmentBrand())
                .equipmentModel(request.equipmentModel())
                .equipmentSerial(request.equipmentSerial())
                .equipmentLocation(request.equipmentLocation())
                .reportedIssue(request.reportedIssue())
                .diagnosis(request.diagnosis())
                .workDone(request.workDone())
                .status(request.status() != null ? request.status() : "PENDING")
                .interventionDate(request.interventionDate())
                .createdBy(userId)
                .assignedTo(request.assignedTo())
                .siteAddress(request.siteAddress())
                .siteCity(request.siteCity())
                .estimatedCost(request.estimatedCost())
                .notes(request.notes())
                .active(true)
                .build();

        if (request.items() != null) {
            var items = request.items().stream().map(itemReq -> {
                var unitPrice = itemReq.unitPrice() != null ? itemReq.unitPrice() : BigDecimal.ZERO;
                var quantity = itemReq.quantity() != null ? itemReq.quantity() : 1;
                return InterventionItem.builder()
                        .intervention(intervention)
                        .type(itemReq.type())
                        .description(itemReq.description())
                        .quantity(quantity)
                        .unitPrice(unitPrice)
                        .total(unitPrice.multiply(BigDecimal.valueOf(quantity)))
                        .build();
            }).toList();
            intervention.setItems(items);
            intervention.setTotalCost(intervention.getItems().stream()
                    .map(InterventionItem::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        var saved = interventionRepository.save(intervention);
        sseManager.sendEvent("INTERVENTION_CREATED",
                InterventionEvent.created(saved.getId(), saved.getReference(), userId));
        return toResponse(saved);
    }

    public Page<InterventionResponse> getInterventions(String status, UUID technicianId, Pageable pageable) {
        if (technicianId != null)
            return (status != null
                    ? interventionRepository.findByActiveTrueAndAssignedToAndStatusOrderByCreatedAtDesc(technicianId, status, pageable)
                    : interventionRepository.findByActiveTrueAndAssignedToOrderByCreatedAtDesc(technicianId, pageable))
                    .map(this::toResponse);
        if (status != null)
            return interventionRepository.findByActiveTrueAndStatusOrderByCreatedAtDesc(status, pageable)
                    .map(this::toResponse);
        return interventionRepository.findByActiveTrueOrderByCreatedAtDesc(pageable)
                .map(this::toResponse);
    }

    public Page<InterventionResponse> getInterventionsByCompanyId(UUID companyId, String status, Pageable pageable) {
        if (status != null)
            return interventionRepository.findByActiveTrueAndClientIdAndStatusOrderByCreatedAtDesc(companyId, status, pageable)
                    .map(this::toResponse);
        return interventionRepository.findByClientIdOrderByCreatedAtDesc(companyId, pageable)
                .map(this::toResponse);
    }

    public InterventionStatsResponse getStats() {
        var countByStatus = interventionRepository.countByStatus().stream()
                .collect(java.util.stream.Collectors.toMap(
                        row -> (String) row[0],
                        row -> (Long) row[1]));
        return new InterventionStatsResponse(
                interventionRepository.countAll(),
                interventionRepository.countActive(),
                countByStatus,
                interventionRepository.countAssigned(),
                interventionRepository.countCompleted(),
                interventionRepository.countPending(),
                interventionRepository.countCancelled(),
                interventionRepository.averageDurationMinutes(),
                interventionRepository.sumEstimatedCost()
        );
    }

    public InterventionResponse getIntervention(UUID id, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);
        return toResponse(intervention);
    }

    @Transactional
    public InterventionResponse updateIntervention(UUID id, CreateInterventionRequest request, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);

        intervention.setReference(request.reference());
        intervention.setClientId(request.clientId());
        intervention.setClientName(request.clientName());
        intervention.setClientEmail(request.clientEmail());
        intervention.setClientPhone(request.clientPhone());
        intervention.setClientAddress(request.clientAddress());
        intervention.setEquipmentType(request.equipmentType());
        intervention.setEquipmentBrand(request.equipmentBrand());
        intervention.setEquipmentModel(request.equipmentModel());
        intervention.setEquipmentSerial(request.equipmentSerial());
        intervention.setEquipmentLocation(request.equipmentLocation());
        intervention.setReportedIssue(request.reportedIssue());
        intervention.setDiagnosis(request.diagnosis());
        intervention.setWorkDone(request.workDone());
        if (request.status() != null) intervention.setStatus(request.status());
        intervention.setInterventionDate(request.interventionDate());
        intervention.setAssignedTo(request.assignedTo());
        intervention.setSiteAddress(request.siteAddress());
        intervention.setSiteCity(request.siteCity());
        intervention.setEstimatedCost(request.estimatedCost());
        intervention.setNotes(request.notes());

        if (request.items() != null) {
            intervention.getItems().clear();
            var items = request.items().stream().map(itemReq -> {
                var unitPrice = itemReq.unitPrice() != null ? itemReq.unitPrice() : BigDecimal.ZERO;
                var quantity = itemReq.quantity() != null ? itemReq.quantity() : 1;
                return InterventionItem.builder()
                        .intervention(intervention)
                        .type(itemReq.type())
                        .description(itemReq.description())
                        .quantity(quantity)
                        .unitPrice(unitPrice)
                        .total(unitPrice.multiply(BigDecimal.valueOf(quantity)))
                        .build();
            }).toList();
            intervention.getItems().addAll(items);
            intervention.setTotalCost(items.stream()
                    .map(InterventionItem::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        }

        return toResponse(interventionRepository.save(intervention));
    }

    @Transactional
    public void deleteIntervention(UUID id, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);
        intervention.setActive(false);
        interventionRepository.save(intervention);
        sseManager.sendEvent("INTERVENTION_DELETED",
                InterventionEvent.deleted(id, intervention.getReference(), userId));
    }

    public Page<InterventionResponse> getClientInterventions(UUID clientId, UUID userId, boolean isAdminOrManager, Pageable pageable) {
        return interventionRepository.findByClientIdOrderByCreatedAtDesc(clientId, pageable)
                .map(this::toResponse);
    }

    public byte[] generatePdf(UUID id, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);
        return PdfService.generate(intervention);
    }

    public void generatePdfToStream(UUID id, UUID userId, boolean isAdminOrManager, java.io.OutputStream out) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);
        PdfService.write(intervention, out);
    }

    @Transactional
    public InterventionResponse updateSchedule(UUID id, UpdateScheduleRequest request, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);
        if (request.departureTime() != null) intervention.setDepartureTime(request.departureTime());
        if (request.arrivalTime() != null) intervention.setArrivalTime(request.arrivalTime());
        if (request.startTime() != null) intervention.setStartTime(request.startTime());
        if (request.endTime() != null) {
            intervention.setEndTime(request.endTime());
            if (intervention.getStartTime() != null)
                intervention.setDurationMinutes((int) java.time.Duration.between(intervention.getStartTime(), request.endTime()).toMinutes());
        }
        return toResponse(interventionRepository.save(intervention));
    }

    @Transactional
    public InterventionResponse updateEquipment(UUID id, UpdateEquipmentRequest request, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);
        if (request.brand() != null) intervention.setEquipmentBrand(request.brand());
        if (request.model() != null) intervention.setEquipmentModel(request.model());
        if (request.serial() != null) intervention.setEquipmentSerial(request.serial());
        if (request.location() != null) intervention.setEquipmentLocation(request.location());
        if (request.problemDescription() != null) intervention.setReportedIssue(request.problemDescription());
        return toResponse(interventionRepository.save(intervention));
    }

    @Transactional
    public InterventionResponse updateDiagnosis(UUID id, UpdateDiagnosisRequest request, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);
        if (request.diagnosis() != null) intervention.setDiagnosis(request.diagnosis());
        if (request.workDone() != null) intervention.setWorkDone(request.workDone());
        return toResponse(interventionRepository.save(intervention));
    }

    @Transactional
    public InterventionResponse updateResult(UUID id, UpdateResultRequest request, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);
        intervention.setResult(request.result());
        if ("UNRESOLVED".equalsIgnoreCase(request.result())) {
            intervention.setFollowUpRecommended(true);
        }
        return toResponse(interventionRepository.save(intervention));
    }

    @Transactional
    public InterventionResponse updateInterventionGps(UUID id, UpdateInterventionRequest request, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);

        if (request.gpsLatitude() != null) {
            intervention.setGpsLatitude(request.gpsLatitude());
        }
        if (request.gpsLongitude() != null) {
            intervention.setGpsLongitude(request.gpsLongitude());
        }

        return toResponse(interventionRepository.save(intervention));
    }

    @Transactional
    public InterventionResponse updateRecommendations(UUID id, UpdateRecommendationsRequest request, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);
        intervention.setRecommendations(request.recommendations());
        return toResponse(interventionRepository.save(intervention));
    }

    @Transactional
    public InterventionResponse addItem(UUID id, ItemRequest request, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);
        var unitPrice = request.unitPrice() != null ? request.unitPrice() : BigDecimal.ZERO;
        var quantity = request.quantity() != null ? request.quantity() : 1;
        var item = InterventionItem.builder()
                .intervention(intervention)
                .type(request.type())
                .description(request.description())
                .quantity(quantity)
                .unitPrice(unitPrice)
                .total(unitPrice.multiply(BigDecimal.valueOf(quantity)))
                .build();
        intervention.getItems().add(item);
        intervention.setTotalCost(intervention.getItems().stream()
                .map(InterventionItem::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        return toResponse(interventionRepository.save(intervention));
    }

    @Transactional
    public InterventionResponse updateItem(UUID interventionId, UUID itemId, ItemRequest request, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(interventionId);
        checkOwnership(intervention, userId, isAdminOrManager);
        var item = intervention.getItems().stream()
                .filter(i -> i.getId().equals(itemId))
                .findFirst()
                .orElseThrow(() -> new NotFoundException("Item not found: " + itemId));
        if (request.type() != null) item.setType(request.type());
        if (request.description() != null) item.setDescription(request.description());
        if (request.quantity() != null) item.setQuantity(request.quantity());
        if (request.unitPrice() != null) item.setUnitPrice(request.unitPrice());
        item.setTotal(item.getUnitPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        intervention.setTotalCost(intervention.getItems().stream()
                .map(InterventionItem::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        return toResponse(interventionRepository.save(intervention));
    }

    @Transactional
    public InterventionResponse removeItem(UUID interventionId, UUID itemId, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(interventionId);
        checkOwnership(intervention, userId, isAdminOrManager);
        var removed = intervention.getItems().removeIf(i -> i.getId().equals(itemId));
        if (!removed) throw new NotFoundException("Item not found: " + itemId);
        intervention.setTotalCost(intervention.getItems().stream()
                .map(InterventionItem::getTotal).reduce(BigDecimal.ZERO, BigDecimal::add));
        return toResponse(interventionRepository.save(intervention));
    }

    @Transactional
    public InterventionResponse assignIntervention(UUID id, UUID assignedTo, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);
        var oldStatus = intervention.getStatus();
        intervention.setAssignedTo(assignedTo);
        statusService.assignIntervention(intervention, userId);
        var saved = interventionRepository.save(intervention);
        sseManager.sendEvent("INTERVENTION_ASSIGNED",
                InterventionEvent.statusChanged(id, saved.getReference(), oldStatus, "ASSIGNED", assignedTo, userId));
        sseManager.sendToUser(assignedTo, "INTERVENTION_ASSIGNED_TO_YOU",
                InterventionEvent.assigned(id, saved.getReference(), assignedTo, userId));
        return toResponse(saved);
    }

    @Transactional
    public InterventionResponse startIntervention(UUID id, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);
        var oldStatus = intervention.getStatus();
        statusService.startIntervention(intervention, userId);
        var saved = interventionRepository.save(intervention);
        sseManager.sendEvent("INTERVENTION_STATUS_CHANGED",
                InterventionEvent.statusChanged(id, saved.getReference(), oldStatus, "IN_PROGRESS", intervention.getAssignedTo(), userId));
        return toResponse(saved);
    }

    @Transactional
    public InterventionResponse closeIntervention(UUID id, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        var oldStatus = intervention.getStatus();
        statusService.closeIntervention(intervention, userId, isAdminOrManager);
        var saved = interventionRepository.save(intervention);
        sseManager.sendEvent("INTERVENTION_STATUS_CHANGED",
                InterventionEvent.statusChanged(id, saved.getReference(), oldStatus, "COMPLETED", intervention.getAssignedTo(), userId));
        return toResponse(saved);
    }

    @Transactional
    public InterventionResponse cancelIntervention(UUID id, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        var oldStatus = intervention.getStatus();
        statusService.cancelIntervention(intervention, userId, isAdminOrManager);
        var saved = interventionRepository.save(intervention);
        sseManager.sendEvent("INTERVENTION_STATUS_CHANGED",
                InterventionEvent.statusChanged(id, saved.getReference(), oldStatus, "CANCELLED", intervention.getAssignedTo(), userId));
        return toResponse(saved);
    }

    @Transactional
    public SyncResponse syncFromMobile(SyncRequest request, UUID userId, boolean isAdminOrManager) {
        var existing = interventionRepository.findByLocalId(request.localId());
        if (existing.isPresent()) {
            var intervention = existing.get();
            checkOwnership(intervention, userId, isAdminOrManager);

            if (request.clientUpdatedAt() != null && intervention.getUpdatedAt() != null
                    && request.clientUpdatedAt().isBefore(intervention.getUpdatedAt())) {
                log.warn("Sync conflict on intervention localId={}: client={}, server={}",
                        request.localId(), request.clientUpdatedAt(), intervention.getUpdatedAt());
                return SyncResponse.conflict(toResponse(intervention),
                        "Server has newer changes. Server updatedAt=" + intervention.getUpdatedAt()
                                + ", client updatedAt=" + request.clientUpdatedAt());
            }

            if (request.status() != null) {
                var currentStatus = intervention.getStatus();
                if (!currentStatus.equals(request.status())) {
                    statusService.validateTransition(intervention, request.status());
                    intervention.setStatus(request.status());
                }
            }
            if (request.interventionDate() != null) intervention.setInterventionDate(request.interventionDate());
            if (request.clientName() != null) intervention.setClientName(request.clientName());
            if (request.clientEmail() != null) intervention.setClientEmail(request.clientEmail());
            if (request.clientPhone() != null) intervention.setClientPhone(request.clientPhone());
            if (request.clientAddress() != null) intervention.setClientAddress(request.clientAddress());
            if (request.equipmentBrand() != null) intervention.setEquipmentBrand(request.equipmentBrand());
            if (request.equipmentModel() != null) intervention.setEquipmentModel(request.equipmentModel());
            if (request.equipmentSerial() != null) intervention.setEquipmentSerial(request.equipmentSerial());
            if (request.reportedIssue() != null) intervention.setReportedIssue(request.reportedIssue());
            if (request.siteAddress() != null) intervention.setSiteAddress(request.siteAddress());
            if (request.siteCity() != null) intervention.setSiteCity(request.siteCity());
            return SyncResponse.updated(toResponse(interventionRepository.save(intervention)));
        }
        var intervention = Intervention.builder()
                .reference(request.reference())
                .clientId(request.clientId())
                .clientName(request.clientName())
                .clientEmail(request.clientEmail())
                .clientPhone(request.clientPhone())
                .clientAddress(request.clientAddress())
                .equipmentType(request.equipmentType())
                .equipmentBrand(request.equipmentBrand())
                .equipmentModel(request.equipmentModel())
                .equipmentSerial(request.equipmentSerial())
                .reportedIssue(request.reportedIssue())
                .status(request.status() != null ? request.status() : "PENDING")
                .interventionDate(request.interventionDate())
                .createdBy(userId)
                .assignedTo(userId)
                .siteAddress(request.siteAddress())
                .siteCity(request.siteCity())
                .localId(request.localId())
                .active(true)
                .build();
        return SyncResponse.created(toResponse(interventionRepository.save(intervention)));
    }

    @Transactional
    public void sendEmailReport(UUID id, String recipientEmail, UUID userId, boolean isAdminOrManager) {
        var intervention = findOrThrow(id);
        checkOwnership(intervention, userId, isAdminOrManager);
        log.info("Sending email report for intervention {} to {}", intervention.getReference(), recipientEmail);
        emailService.sendInterventionReport(intervention, recipientEmail);
    }

    private InterventionResponse toResponse(Intervention i) {
        var items = i.getItems() != null
                ? i.getItems().stream().<ItemResponse>map(item -> new ItemResponse(
                        item.getId(), item.getType(), item.getDescription(),
                        item.getQuantity(), item.getUnitPrice(), item.getTotal(),
                        item.getCreatedAt())).toList()
                : List.<ItemResponse>of();

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
    }
}
