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

    @Size(min = 8)
    String password,

    @NotBlank
    @Pattern(regexp = "ADMIN|MANAGER|TECHNICIAN|CLIENT_ADMIN|CLIENT_USER|CLIENT_VIEWER",
             message = "Invalid role: must be one of ADMIN, MANAGER, TECHNICIAN, CLIENT_ADMIN, CLIENT_USER, CLIENT_VIEWER")
    String role,

    String phone
) {}
