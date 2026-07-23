package tg.ngstars.report.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record PdfTemplateResponse(
    UUID id,
    String name,
    String description,
    String templateType,
    String config,
    Boolean isDefault,
    String createdBy,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
