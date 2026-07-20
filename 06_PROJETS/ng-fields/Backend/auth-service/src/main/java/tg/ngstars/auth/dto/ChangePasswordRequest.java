package tg.ngstars.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordRequest(
    @NotBlank String currentPassword,
    @NotBlank @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caracteres") String newPassword
) {}
