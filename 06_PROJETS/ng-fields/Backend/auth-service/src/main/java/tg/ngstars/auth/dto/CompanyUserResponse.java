package tg.ngstars.auth.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CompanyUserResponse(
    UUID id,
    UUID companyId,
    UUID keycloakUserId,
    String email,
    String firstName,
    String lastName,
    String role,
    boolean active,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
