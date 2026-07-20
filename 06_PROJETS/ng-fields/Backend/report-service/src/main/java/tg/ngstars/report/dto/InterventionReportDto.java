package tg.ngstars.report.dto;

import java.math.BigDecimal;
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
    Boolean billable,
    BigDecimal billingAmount,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
