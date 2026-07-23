package tg.ngstars.interv.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record InterventionEvent(
    String eventType,
    UUID interventionId,
    String reference,
    String status,
    String oldStatus,
    UUID assignedTo,
    UUID triggeredBy,
    OffsetDateTime timestamp,
    String message
) {
    public static InterventionEvent statusChanged(UUID interventionId, String reference,
            String oldStatus, String newStatus, UUID assignedTo, UUID triggeredBy) {
        return new InterventionEvent(
                "INTERVENTION_STATUS_CHANGED",
                interventionId, reference, newStatus, oldStatus,
                assignedTo, triggeredBy, OffsetDateTime.now(),
                "Intervention " + reference + " : " + oldStatus + " -> " + newStatus);
    }

    public static InterventionEvent assigned(UUID interventionId, String reference,
            UUID assignedTo, UUID triggeredBy) {
        return new InterventionEvent(
                "INTERVENTION_ASSIGNED",
                interventionId, reference, "ASSIGNED", null,
                assignedTo, triggeredBy, OffsetDateTime.now(),
                "Intervention " + reference + " assignee");
    }

    public static InterventionEvent created(UUID interventionId, String reference,
            UUID triggeredBy) {
        return new InterventionEvent(
                "INTERVENTION_CREATED",
                interventionId, reference, "PENDING", null,
                null, triggeredBy, OffsetDateTime.now(),
                "Nouvelle intervention " + reference);
    }

    public static InterventionEvent deleted(UUID interventionId, String reference,
            UUID triggeredBy) {
        return new InterventionEvent(
                "INTERVENTION_DELETED",
                interventionId, reference, null, null,
                null, triggeredBy, OffsetDateTime.now(),
                "Intervention " + reference + " supprimee");
    }
}
