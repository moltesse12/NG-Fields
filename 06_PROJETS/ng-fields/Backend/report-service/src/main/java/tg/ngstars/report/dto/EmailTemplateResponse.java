package tg.ngstars.report.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record EmailTemplateResponse(
    UUID id,
    String name,
    String description,
    String templateKey,
    String subject,
    String bodyHtml,
    Boolean isActive,
    String createdBy,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
