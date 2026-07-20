package tg.ngstars.client.dto;

import java.util.UUID;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ContactDto(
    UUID id,
    @NotBlank @Size(max = 150) String fullName,
    @Email @Size(max = 150) String email,
    @Size(max = 30) String phone,
    @Size(max = 50) String role
) {}
