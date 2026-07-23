package tg.ngstars.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddCompanyUserRequest(
    @NotBlank @Email String email,
    @NotBlank @Size(max = 100) String firstName,
    @NotBlank @Size(max = 100) String lastName,
    @NotBlank @Pattern(regexp = "CLIENT_ADMIN|CLIENT_USER|CLIENT_VIEWER",
                       message = "Role invalide : CLIENT_ADMIN, CLIENT_USER, CLIENT_VIEWER")
    String role
) {}
