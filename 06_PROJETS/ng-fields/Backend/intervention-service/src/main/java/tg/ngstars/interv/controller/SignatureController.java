package tg.ngstars.interv.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import tg.ngstars.interv.dto.SignatureRequest;
import tg.ngstars.interv.dto.SignatureResponse;
import tg.ngstars.interv.service.SignatureService;

import java.util.UUID;

@RestController
@RequestMapping("/api/interventions/{id}/signatures")
public class SignatureController {

    private final SignatureService signatureService;

    public SignatureController(SignatureService signatureService) {
        this.signatureService = signatureService;
    }

    @PostMapping("/client")
    @PreAuthorize("hasAnyRole('TECHNICIAN','MANAGER','ADMIN')")
    public ResponseEntity<SignatureResponse> signClient(
            @PathVariable UUID id,
            @Valid @RequestBody SignatureRequest req) throws java.io.IOException {
        String url = signatureService.signClient(id, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SignatureResponse.created("Signature client enregistrée", url));
    }

    @PostMapping("/technician")
    @PreAuthorize("hasAnyRole('TECHNICIAN','MANAGER','ADMIN')")
    public ResponseEntity<SignatureResponse> signTechnician(
            @PathVariable UUID id,
            @Valid @RequestBody SignatureRequest req) throws java.io.IOException {
        String url = signatureService.signTechnician(id, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SignatureResponse.created("Signature technicien enregistrée", url));
    }

    @PostMapping("/manager")
    @PreAuthorize("hasAnyRole('MANAGER','ADMIN')")
    public ResponseEntity<SignatureResponse> signManager(
            @PathVariable UUID id,
            @Valid @RequestBody SignatureRequest req) throws java.io.IOException {
        String url = signatureService.signManager(id, req);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(SignatureResponse.created("Signature manager enregistrée. Intervention validée.", url));
    }
}
