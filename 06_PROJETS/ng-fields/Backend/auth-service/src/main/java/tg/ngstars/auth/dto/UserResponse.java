package tg.ngstars.auth.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record UserResponse(
    UUID id,
    UUID keycloakId,
    String username,
    String email,
    String firstName,
    String lastName,
    String role,
    String phone,
    boolean active,
    UUID companyId,
    boolean mustChangePassword,
    OffsetDateTime createdAt,
    OffsetDateTime updatedAt
) {}
