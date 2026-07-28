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
             message = "Password must be at least 8 characters with uppercase, digit, and special character")
    String password,

    @NotBlank
    @Pattern(regexp = "ADMIN|MANAGER|TECHNICIAN|CLIENT_ADMIN|CLIENT_USER|CLIENT_VIEWER",
             message = "Invalid role: must be one of ADMIN, MANAGER, TECHNICIAN, CLIENT_ADMIN, CLIENT_USER, CLIENT_VIEWER")
    String role,

    String phone
) {}
