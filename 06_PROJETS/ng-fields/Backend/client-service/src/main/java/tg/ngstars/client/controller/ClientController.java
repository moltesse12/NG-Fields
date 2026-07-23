package tg.ngstars.client.controller;

import java.util.List;
import java.util.UUID;

import jakarta.validation.Valid;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import tg.ngstars.client.dto.ClientResponse;
import tg.ngstars.client.dto.ContactDto;
import tg.ngstars.client.dto.CreateClientRequest;
import tg.ngstars.client.dto.CreateContactRequest;
import tg.ngstars.client.dto.UpdateClientRequest;
import tg.ngstars.client.service.ClientService;

@RestController
@RequestMapping("/api/clients")
@Tag(name = "Clients", description = "Gestion des fiches clients et contacts")
public class ClientController {

    private final ClientService clientService;

    public ClientController(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Creer un client", description = "Cree une fiche client. La reference est generee automatiquement.")
    @ApiResponse(responseCode = "201", description = "Client cree")
    @ApiResponse(responseCode = "409", description = "Email deja utilise")
    public ResponseEntity<ClientResponse> createClient(
            @Valid @RequestBody CreateClientRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clientService.createClient(request, jwt.getSubject()));
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    @Operation(summary = "Lister les clients", description = "Pagine, trie par nom d'entreprise.")
    @ApiResponse(responseCode = "200", description = "Page de resultats")
    public ResponseEntity<Page<ClientResponse>> listClients(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(clientService.listClients(page, size));
    }

    @GetMapping("/search")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    @Operation(summary = "Rechercher des clients", description = "Recherche ILIKE sur companyName, contactName, email, phone.")
    @ApiResponse(responseCode = "200", description = "Page de resultats")
    public ResponseEntity<Page<ClientResponse>> searchClients(
            @RequestParam @Parameter(description = "Terme de recherche") String q,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        return ResponseEntity.ok(clientService.searchClients(q, page, size));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    @Operation(summary = "Obtenir un client", description = "Detail complet avec contacts.")
    @ApiResponse(responseCode = "200", description = "Client trouve")
    @ApiResponse(responseCode = "404", description = "Client introuvable")
    public ResponseEntity<ClientResponse> getClient(@PathVariable UUID id) {
        return ResponseEntity.ok(clientService.getClient(id));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Mettre a jour un client", description = "Met a jour les infos et synchronise avec intervention-service.")
    @ApiResponse(responseCode = "200", description = "Client mis a jour")
    public ResponseEntity<ClientResponse> updateClient(
            @PathVariable UUID id,
            @Valid @RequestBody UpdateClientRequest request) {
        return ResponseEntity.ok(clientService.updateClient(id, request));
    }

    @PostMapping("/{id}/reactivate")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Reactiver un client", description = "Repasse active=true sur un client desactive.")
    @ApiResponse(responseCode = "200", description = "Client reactive")
    @ApiResponse(responseCode = "404", description = "Client introuvable")
    public ResponseEntity<ClientResponse> reactivateClient(@PathVariable UUID id) {
        return ResponseEntity.ok(clientService.reactivateClient(id));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Desactiver un client", description = "Soft delete : passe active=false.")
    @ApiResponse(responseCode = "204", description = "Client desactive")
    public ResponseEntity<Void> deactivateClient(@PathVariable UUID id) {
        clientService.deactivateClient(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{clientId}/contacts")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ContactDto> addContact(
            @PathVariable UUID clientId,
            @Valid @RequestBody CreateContactRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(clientService.addContact(clientId, request));
    }

    @GetMapping("/{clientId}/contacts")
    @PreAuthorize("hasAnyRole('ADMIN', 'MANAGER', 'TECHNICIAN')")
    public ResponseEntity<List<ContactDto>> getContacts(@PathVariable UUID clientId) {
        return ResponseEntity.ok(clientService.getContacts(clientId));
    }

    @DeleteMapping("/{clientId}/contacts/{contactId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> removeContact(
            @PathVariable UUID clientId,
            @PathVariable UUID contactId) {
        clientService.removeContact(clientId, contactId);
        return ResponseEntity.noContent().build();
    }
}
