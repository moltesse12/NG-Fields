package tg.ngstars.auth.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CompanyResponse(
    UUID id,
    String name,
    String email,
    String phone,
    String address,
    String contactName,
    String contactPhone,
    UUID keycloakOrganizationId,
    boolean active,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
