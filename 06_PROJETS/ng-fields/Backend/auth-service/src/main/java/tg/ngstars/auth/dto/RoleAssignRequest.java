package tg.ngstars.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RoleAssignRequest(
    @NotBlank
    @Pattern(regexp = "ADMIN|MANAGER|TECHNICIAN|CLIENT_ADMIN|CLIENT_USER|CLIENT_VIEWER")
    String role
) {}
