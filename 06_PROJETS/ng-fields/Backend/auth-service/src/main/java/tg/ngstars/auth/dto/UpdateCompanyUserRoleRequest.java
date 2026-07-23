package tg.ngstars.auth.dto;

import jakarta.validation.constraints.Pattern;

public record UpdateCompanyUserRoleRequest(
    @Pattern(regexp = "CLIENT_ADMIN|CLIENT_USER|CLIENT_VIEWER",
             message = "Role invalide : CLIENT_ADMIN, CLIENT_USER, CLIENT_VIEWER")
    String role
) {}
