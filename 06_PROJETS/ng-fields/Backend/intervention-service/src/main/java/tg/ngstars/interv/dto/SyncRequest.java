package tg.ngstars.interv.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SyncRequest(
    @NotBlank String reference,
    @NotNull UUID clientId,
    String clientName,
    String clientEmail,
    String clientPhone,
    String clientAddress,
    String equipmentType,
    String equipmentBrand,
    String equipmentModel,
    String equipmentSerial,
    String reportedIssue,
    String status,
    OffsetDateTime interventionDate,
    String siteAddress,
    String siteCity,
    @NotBlank String localId,
    OffsetDateTime clientUpdatedAt
) {}
