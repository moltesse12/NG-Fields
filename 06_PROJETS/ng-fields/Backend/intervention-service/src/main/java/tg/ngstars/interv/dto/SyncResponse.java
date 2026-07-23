package tg.ngstars.interv.dto;

import java.time.OffsetDateTime;

public record SyncResponse(
    InterventionResponse intervention,
    SyncAction action,
    boolean conflict,
    String conflictMessage,
    OffsetDateTime serverUpdatedAt
) {

    public enum SyncAction {
        CREATED,
        UPDATED,
        CONFLICT
    }

    public static SyncResponse created(InterventionResponse intervention) {
        return new SyncResponse(intervention, SyncAction.CREATED, false, null, intervention.updatedAt());
    }

    public static SyncResponse updated(InterventionResponse intervention) {
        return new SyncResponse(intervention, SyncAction.UPDATED, false, null, intervention.updatedAt());
    }

    public static SyncResponse conflict(InterventionResponse serverVersion, String message) {
        return new SyncResponse(serverVersion, SyncAction.CONFLICT, true, message, serverVersion.updatedAt());
    }

    public static SyncResponse error(String message) {
        return new SyncResponse(null, SyncAction.CONFLICT, true, message, null);
    }
}
