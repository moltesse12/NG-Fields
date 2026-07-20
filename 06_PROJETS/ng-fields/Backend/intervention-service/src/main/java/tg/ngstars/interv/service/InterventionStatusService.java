package tg.ngstars.interv.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tg.ngstars.common.exception.ConflictException;
import tg.ngstars.common.exception.ForbiddenException;
import tg.ngstars.interv.model.Intervention;
import tg.ngstars.interv.model.InterventionStatus;

import java.time.OffsetDateTime;
import java.util.UUID;

@Service
public class InterventionStatusService {

    private static final Logger log = LoggerFactory.getLogger(InterventionStatusService.class);

    public InterventionStatusService() {
    }

    public void validateTransition(Intervention intervention, String targetStatus) {
        var current = intervention.getStatus();
        var currentEnum = InterventionStatus.fromString(current);
        if (!currentEnum.canTransitionTo(targetStatus)) {
            throw new ConflictException(
                    "Cannot transition from " + current + " to " + targetStatus);
        }
    }

    @Transactional
    public void assignIntervention(Intervention intervention, UUID assignedBy) {
        validateTransition(intervention, InterventionStatus.ASSIGNED.name());
        intervention.setStatus(InterventionStatus.ASSIGNED.name());
        log.info("Intervention {} → ASSIGNED (by {})", intervention.getReference(), assignedBy);
    }

    @Transactional
    public void startIntervention(Intervention intervention, UUID startedBy) {
        validateTransition(intervention, InterventionStatus.IN_PROGRESS.name());
        intervention.setStatus(InterventionStatus.IN_PROGRESS.name());
        log.info("Intervention {} → IN_PROGRESS (by {})", intervention.getReference(), startedBy);
    }

    @Transactional
    public void closeIntervention(Intervention intervention, UUID userId, boolean isAdminOrManager) {
        if (!isAdminOrManager
                && (intervention.getAssignedTo() == null || !intervention.getAssignedTo().equals(userId))) {
            throw new ForbiddenException("Not assigned to this intervention");
        }

        if (intervention.getClientSignature() == null
                || intervention.getTechnicianSignature() == null
                || intervention.getManagerSignature() == null) {
            throw new ConflictException(
                    "Toutes les signatures (client, technicien, manager) sont requises pour clôturer l'intervention");
        }

        validateTransition(intervention, InterventionStatus.COMPLETED.name());
        intervention.setStatus(InterventionStatus.COMPLETED.name());
        intervention.setSignedAt(OffsetDateTime.now());
        log.info("Intervention {} → COMPLETED (by {})", intervention.getReference(), userId);
    }

    @Transactional
    public void cancelIntervention(Intervention intervention, UUID userId, boolean isAdminOrManager) {
        if (!isAdminOrManager) {
            throw new ForbiddenException("Only admin or manager can cancel an intervention");
        }

        validateTransition(intervention, InterventionStatus.CANCELLED.name());
        intervention.setStatus(InterventionStatus.CANCELLED.name());
        log.info("Intervention {} → CANCELLED (by {})", intervention.getReference(), userId);
    }
}
