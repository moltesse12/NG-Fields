package tg.ngstars.auth.dto;

import jakarta.validation.constraints.*;

public record CreateUserRequest(
    @NotBlank @Size(min = 3, max = 50)
    String username,

    @NotBlank @Email
    String email,

    @NotBlank @Size(max = 100)
    String firstName,

    @NotBlank @Size(max = 100)
    String lastName,

    @NotBlank @Size(min = 8)
    @Pattern(regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
             message = "Le mot de passe doit contenir au moins 8 caracteres, une majuscule, un chiffre et un caractere special")
    String password,

    @NotBlank
    @Pattern(regexp = "ADMIN|MANAGER|TECHNICIAN|CLIENT_ADMIN|CLIENT_USER|CLIENT_VIEWER",
             message = "Role invalide : ADMIN, MANAGER, TECHNICIAN, CLIENT_ADMIN, CLIENT_USER, CLIENT_VIEWER")
    String role,

    String phone
) {}
