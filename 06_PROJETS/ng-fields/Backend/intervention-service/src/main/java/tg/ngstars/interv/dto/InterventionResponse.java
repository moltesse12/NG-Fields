package tg.ngstars.interv.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record InterventionResponse(
    UUID id,
    String reference,
    UUID clientId,
    String clientName,
    String clientEmail,
    String clientPhone,
    String clientAddress,
    String equipmentType,
    String equipmentBrand,
    String equipmentModel,
    String equipmentSerial,
    String equipmentLocation,
    String reportedIssue,
    String openprojectTicketId,
    String openprojectTicketUrl,
    String diagnosis,
    String workDone,
    String status,
    OffsetDateTime interventionDate,
    UUID createdBy,
    UUID assignedTo,
    String siteAddress,
    String siteCity,
    BigDecimal estimatedCost,
    Double gpsLatitude,
    Double gpsLongitude,
    BigDecimal totalCost,
    String clientSignature,
    String technicianSignature,
    String managerSignature,
    OffsetDateTime signedAt,
    OffsetDateTime departureTime,
    OffsetDateTime arrivalTime,
    OffsetDateTime startTime,
    OffsetDateTime endTime,
    Integer durationMinutes,
    String result,
    String recommendations,
    Boolean billable,
    BigDecimal billingAmount,
    String billingNotes,
    String localId,
    String notes,
    boolean active,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt,
    List<ItemResponse> items
) {

    public record ItemResponse(
        UUID id,
        String type,
        String description,
        int quantity,
        BigDecimal unitPrice,
        BigDecimal total,
        OffsetDateTime createdAt
    ) {}
}
