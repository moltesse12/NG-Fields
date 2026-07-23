package tg.ngstars.interv.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record SyncBatchResponse(
    List<SyncResponse> results,
    int totalProcessed,
    int totalCreated,
    int totalUpdated,
    int totalConflicts,
    long durationMs
) {
    public static SyncBatchResponse of(List<SyncResponse> results, long durationMs) {
        int created = 0, updated = 0, conflicts = 0;
        for (var r : results) {
            switch (r.action()) {
                case CREATED -> created++;
                case UPDATED -> updated++;
                case CONFLICT -> conflicts++;
            }
        }
        return new SyncBatchResponse(results, results.size(), created, updated, conflicts, durationMs);
    }
}
