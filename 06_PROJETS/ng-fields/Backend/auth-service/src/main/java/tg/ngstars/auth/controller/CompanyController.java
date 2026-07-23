package tg.ngstars.auth.controller;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import tg.ngstars.auth.dto.AddCompanyUserRequest;
import tg.ngstars.auth.dto.CompanyResponse;
import tg.ngstars.auth.dto.CompanyUserResponse;
import tg.ngstars.auth.dto.CreateCompanyRequest;
import tg.ngstars.auth.dto.UpdateCompanyRequest;
import tg.ngstars.auth.dto.UpdateCompanyUserRoleRequest;
import tg.ngstars.auth.service.CompanyService;

@RestController
@RequestMapping("/api/admin/companies")
@PreAuthorize("hasRole('ADMIN')")
public class CompanyController {

    private final CompanyService companyService;

    public CompanyController(CompanyService companyService) {
        this.companyService = companyService;
    }

    @PostMapping
    public ResponseEntity<CompanyResponse> createCompany(
            @Valid @RequestBody CreateCompanyRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(companyService.createCompany(request, jwt.getSubject()));
    }

    @GetMapping
    public ResponseEntity<Page<CompanyResponse>> getCompanies(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(companyService.getAllCompanies(PageRequest.of(page, size)));
    }

    @GetMapping("/active")
    public ResponseEntity<List<CompanyResponse>> getActiveCompanies() {
        return ResponseEntity.ok(companyService.getActiveCompanies());
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompanyResponse> getCompany(@PathVariable UUID id) {
        return ResponseEntity.ok(companyService.getCompany(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompanyResponse> updateCompany(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateCompanyRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(companyService.updateCompany(id, request, jwt.getSubject()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deactivateCompany(
            @PathVariable UUID id,
            @AuthenticationPrincipal Jwt jwt) {
        companyService.deactivateCompany(id, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }

    // ── Company Users ─────────────────────────────────────────

    @PostMapping("/{companyId}/users")
    public ResponseEntity<CompanyUserResponse> addCompanyUser(
            @PathVariable UUID companyId,
            @Valid @RequestBody AddCompanyUserRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(companyService.addCompanyUser(companyId, request, jwt.getSubject()));
    }

    @GetMapping("/{companyId}/users")
    public ResponseEntity<List<CompanyUserResponse>> getCompanyUsers(@PathVariable UUID companyId) {
        return ResponseEntity.ok(companyService.getCompanyUsers(companyId));
    }

    @PatchMapping("/{companyId}/users/{userId}/role")
    public ResponseEntity<CompanyUserResponse> updateCompanyUserRole(
            @PathVariable UUID companyId,
            @PathVariable UUID userId,
            @Valid @RequestBody UpdateCompanyUserRoleRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(companyService.updateCompanyUserRole(companyId, userId, request, jwt.getSubject()));
    }

    @DeleteMapping("/{companyId}/users/{userId}")
    public ResponseEntity<Void> deactivateCompanyUser(
            @PathVariable UUID companyId,
            @PathVariable UUID userId,
            @AuthenticationPrincipal Jwt jwt) {
        companyService.deactivateCompanyUser(companyId, userId, jwt.getSubject());
        return ResponseEntity.noContent().build();
    }
}
