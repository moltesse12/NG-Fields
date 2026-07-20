package tg.ngstars.client.dto;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record ClientResponse(
    UUID id,
    String reference,
    String companyName,
    String contactName,
    String email,
    String phone,
    String address,
    Double latitude,
    Double longitude,
    boolean active,
    OffsetDateTime createdAt,
    List<ContactDto> contacts
) {}
