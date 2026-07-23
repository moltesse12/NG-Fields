package tg.ngstars.report.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record InterventionReportDto(
    UUID id,
    String reference,
    String clientName,
    String clientEmail,
    String clientPhone,
    String equipmentType,
    String equipmentBrand,
    String equipmentModel,
    String reportedIssue,
    String diagnosis,
    String workDone,
    String status,
    UUID assignedTo,
    String result,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
