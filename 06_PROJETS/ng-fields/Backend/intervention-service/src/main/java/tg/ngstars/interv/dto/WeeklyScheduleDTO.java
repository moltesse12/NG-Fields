package tg.ngstars.interv.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record WeeklyScheduleDTO(
    UUID technicianId,
    String technicianName,
    List<ScheduleDayDTO> days
) {
    public record ScheduleDayDTO(
        OffsetDateTime date,
        List<ScheduleSlotDTO> interventions
    ) {}

    public record ScheduleSlotDTO(
        UUID interventionId,
        String reference,
        String clientName,
        String status,
        String siteAddress,
        String siteCity,
        OffsetDateTime startTime,
        OffsetDateTime endTime,
        Integer durationMinutes
    ) {}
}
