package tg.ngstars.auth.dto;

import jakarta.validation.constraints.*;

public record UpdateUserRequest(
    @NotBlank @Size(min = 3, max = 50)
    String username,

    @NotBlank @Email
    String email,

    @NotBlank @Size(max = 100)
    String firstName,

    @NotBlank @Size(max = 100)
    String lastName,

    @Size(min = 6)
    String password,

    @Pattern(regexp = "ADMIN|MANAGER|TECHNICIAN|CLIENT_ADMIN|CLIENT_USER|CLIENT_VIEWER",
             message = "Role invalide : ADMIN, MANAGER, TECHNICIAN, CLIENT_ADMIN, CLIENT_USER, CLIENT_VIEWER")
    String role,

    String phone
) {}
