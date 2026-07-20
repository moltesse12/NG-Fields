package tg.ngstars.interv.model;

import java.util.Set;

public enum InterventionStatus {

    PENDING(Set.of("ASSIGNED", "CANCELLED")),
    ASSIGNED(Set.of("IN_PROGRESS", "CANCELLED")),
    IN_PROGRESS(Set.of("COMPLETED", "CANCELLED")),
    COMPLETED(Set.of()),
    CANCELLED(Set.of());

    private final Set<String> allowedTransitions;

    InterventionStatus(Set<String> allowedTransitions) {
        this.allowedTransitions = allowedTransitions;
    }

    public boolean canTransitionTo(String targetStatus) {
        return allowedTransitions.contains(targetStatus);
    }

    public static InterventionStatus fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Status cannot be null or blank");
        }
        try {
            return valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Invalid status: '" + value + "'. Valid values: PENDING, ASSIGNED, IN_PROGRESS, COMPLETED, CANCELLED");
        }
    }
}
