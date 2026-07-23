package tg.ngstars.client.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ClientListDTO(
    UUID id,
    String reference,
    String companyName,
    String contactName,
    String email,
    String phone,
    boolean active,
    OffsetDateTime createdAt
) {}
