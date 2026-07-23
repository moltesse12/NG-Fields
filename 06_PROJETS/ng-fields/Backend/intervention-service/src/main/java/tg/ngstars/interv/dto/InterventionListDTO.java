package tg.ngstars.interv.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public record InterventionListDTO(
    UUID id,
    String reference,
    String clientName,
    String equipmentType,
    String equipmentBrand,
    String status,
    String result,
    OffsetDateTime interventionDate,
    UUID assignedTo,
    BigDecimal estimatedCost,
    BigDecimal totalCost,
    Integer durationMinutes,
    boolean active,
    OffsetDateTime createdAt
) {}
