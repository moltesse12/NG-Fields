package tg.ngstars.client.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import tg.ngstars.client.dto.CreateClientRequest;
import tg.ngstars.client.dto.CreateContactRequest;
import tg.ngstars.client.dto.UpdateClientRequest;
import tg.ngstars.common.exception.ConflictException;
import tg.ngstars.common.exception.NotFoundException;
import tg.ngstars.client.model.Client;
import tg.ngstars.client.model.Contact;
import tg.ngstars.client.repository.ClientRepository;
import tg.ngstars.client.repository.ContactRepository;

@ExtendWith(MockitoExtension.class)
class ClientServiceTest {

    @Mock ClientRepository clientRepository;
    @Mock ReferenceGeneratorService referenceGeneratorService;
    @Mock ContactRepository contactRepository;
    ClientService service;

    UUID clientId = UUID.randomUUID();
    Client client;

    @BeforeEach
    void setUp() {
        service = new ClientService(clientRepository, referenceGeneratorService, contactRepository);
        client = Client.builder()
                .id(clientId)
                .reference("CLT-0001")
                .companyName("ACME Inc")
                .contactName("John")
                .email("acme@test.com")
                .phone("123456")
                .address("123 Main St")
                .latitude(48.85)
                .longitude(2.35)
                .active(true)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }

    @Test
    void createClient_shouldGenerateReference() {
        var req = new CreateClientRequest("ACME Inc", "John", "acme@test.com", "123456", "123 Main St", 48.85, 2.35);
        when(clientRepository.existsByEmail("acme@test.com")).thenReturn(false);
        when(referenceGeneratorService.generateNextReference()).thenReturn("CLT-0001");
        when(clientRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.createClient(req, "admin");

        assertEquals("CLT-0001", result.reference());
        assertEquals("ACME Inc", result.companyName());
        assertEquals("acme@test.com", result.email());
    }

    @Test
    void createClient_duplicateEmail_throwsConflict() {
        var req = new CreateClientRequest("ACME Inc", "John", "acme@test.com", "123456", "123 Main St", 48.85, 2.35);
        when(clientRepository.existsByEmail("acme@test.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.createClient(req, "admin"));
    }

    @Test
    void getClient_shouldReturnClient() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        var result = service.getClient(clientId);
        assertEquals("ACME Inc", result.companyName());
    }

    @Test
    void getClient_notFound_throwsNotFound() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getClient(clientId));
    }

    @Test
    void listClients_shouldReturnPage() {
        var page = new PageImpl<>(java.util.List.of(client));
        when(clientRepository.findByActiveTrue(any(Pageable.class))).thenReturn(page);

        var result = service.listClients(0, 10);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void updateClient_shouldUpdateFields() {
        var req = new UpdateClientRequest("NewCo", "Jane", "new@test.com", "999", "New addr", 47.0, 1.0);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(clientRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.updateClient(clientId, req);

        assertEquals("NewCo", result.companyName());
        assertEquals("Jane", result.contactName());
        assertEquals("new@test.com", result.email());
    }

    @Test
    void updateClient_duplicateEmail_throwsConflict() {
        var req = new UpdateClientRequest("NewCo", "Jane", "other@test.com", "999", "Addr", null, null);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(clientRepository.existsByEmail("other@test.com")).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.updateClient(clientId, req));
    }

    @Test
    void deactivateClient_shouldSetInactive() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(clientRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        service.deactivateClient(clientId);

        assertFalse(client.getActive());
    }

    @Test
    void deactivateClient_notFound_throwsNotFound() {
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.deactivateClient(clientId));
    }

    @Test
    void searchClients_shouldReturnResults() {
        var page = new PageImpl<>(java.util.List.of(client));
        when(clientRepository.search(eq("ACME"), any(Pageable.class))).thenReturn(page);

        var result = service.searchClients("ACME", 0, 10);
        assertEquals(1, result.getContent().size());
    }

    @Test
    void updateClient_sameEmail_shouldNotCheckConflict() {
        var req = new UpdateClientRequest("NewCo", "Jane", "acme@test.com", "999", "Addr", null, null);
        when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
        when(clientRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.updateClient(clientId, req);
        assertEquals("NewCo", result.companyName());
    }

    @Test
    void updateClient_notFound_throwsNotFound() {
        var req = new UpdateClientRequest("NewCo", "Jane", "new@test.com", "999", "Addr", null, null);
        when(clientRepository.findById(clientId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.updateClient(clientId, req));
    }

    @Test
    void addContact_shouldSaveAndReturn() {
        var client = createClientEntity();
        when(clientRepository.findById(any(UUID.class))).thenReturn(Optional.of(client));
        when(contactRepository.save(any())).thenAnswer(inv -> {
            var c = inv.getArgument(0, Contact.class);
            c.setId(UUID.randomUUID());
            return c;
        });

        var request = new CreateContactRequest("Jean Dupont", "jean@example.com", "+22890123456", "Responsable");
        var result = service.addContact(client.getId(), request);

        assertNotNull(result);
        assertEquals("Jean Dupont", result.fullName());
        verify(contactRepository).save(any());
    }

    @Test
    void addContact_clientNotFound_shouldThrow() {
        when(clientRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        var request = new CreateContactRequest("Jean Dupont", "jean@example.com", "+22890123456", "Responsable");
        assertThrows(NotFoundException.class, () -> service.addContact(UUID.randomUUID(), request));
    }

    @Test
    void getContacts_shouldReturnList() {
        var client = createClientEntity();
        when(clientRepository.existsById(any(UUID.class))).thenReturn(true);
        var contact = Contact.builder()
                .id(UUID.randomUUID()).client(client)
                .fullName("Jean Dupont").email("jean@example.com")
                .phone("+22890123456").role("Responsable").active(true)
                .build();
        when(contactRepository.findByClientIdAndActiveTrue(any())).thenReturn(List.of(contact));

        var result = service.getContacts(client.getId());
        assertEquals(1, result.size());
        assertEquals("Jean Dupont", result.getFirst().fullName());
    }

    @Test
    void getContacts_clientNotFound_shouldThrow() {
        when(clientRepository.existsById(any(UUID.class))).thenReturn(false);
        assertThrows(NotFoundException.class, () -> service.getContacts(UUID.randomUUID()));
    }

    @Test
    void removeContact_shouldDeactivate() {
        var client = createClientEntity();
        var contact = Contact.builder()
                .id(UUID.randomUUID()).client(client)
                .fullName("Jean Dupont").active(true)
                .build();
        when(contactRepository.findById(any(UUID.class))).thenReturn(Optional.of(contact));
        when(contactRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        service.removeContact(client.getId(), contact.getId());
        assertFalse(contact.getActive());
    }

    @Test
    void removeContact_wrongClient_shouldThrow() {
        var client = createClientEntity();
        var otherClient = Client.builder().id(UUID.randomUUID()).build();
        var contact = Contact.builder()
                .id(UUID.randomUUID()).client(otherClient)
                .fullName("Jean Dupont").active(true)
                .build();
        when(contactRepository.findById(any(UUID.class))).thenReturn(Optional.of(contact));

        assertThrows(NotFoundException.class, () -> service.removeContact(client.getId(), contact.getId()));
    }

    @Test
    void removeContact_notFound_shouldThrow() {
        when(contactRepository.findById(any(UUID.class))).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.removeContact(UUID.randomUUID(), UUID.randomUUID()));
    }

    private Client createClientEntity() {
        return Client.builder()
                .id(UUID.randomUUID())
                .reference("CLT-0001")
                .companyName("ACME Inc")
                .contactName("John")
                .email("acme@test.com")
                .phone("123456")
                .address("123 Main St")
                .latitude(48.85)
                .longitude(2.35)
                .active(true)
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();
    }
}
