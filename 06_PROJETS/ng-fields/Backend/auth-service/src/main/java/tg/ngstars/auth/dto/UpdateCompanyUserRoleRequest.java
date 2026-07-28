package tg.ngstars.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record UpdateCompanyUserRoleRequest(
    @NotBlank
    @Pattern(regexp = "CLIENT_ADMIN|CLIENT_USER|CLIENT_VIEWER",
             message = "Invalid role: must be one of CLIENT_ADMIN, CLIENT_USER, CLIENT_VIEWER")
    String role
) {}
