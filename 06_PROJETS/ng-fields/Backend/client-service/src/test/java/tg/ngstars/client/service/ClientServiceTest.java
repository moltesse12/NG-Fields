package tg.ngstars.client.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import tg.ngstars.client.dto.*;
import tg.ngstars.common.exception.ConflictException;
import tg.ngstars.common.exception.NotFoundException;
import tg.ngstars.client.model.Client;
import tg.ngstars.client.model.Contact;
import tg.ngstars.client.model.ContactRole;
import tg.ngstars.client.repository.ClientRepository;
import tg.ngstars.client.repository.ContactRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("ClientService")
class ClientServiceTest {

    @Mock private ClientRepository clientRepository;
    @Mock private ReferenceGeneratorService referenceGeneratorService;
    @Mock private ContactRepository contactRepository;
    @Mock private InterventionSyncClient interventionSyncClient;

    @InjectMocks
    private ClientService clientService;

    private UUID clientId;
    private Client client;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        client = Client.builder()
                .id(clientId)
                .reference("CLT-0001")
                .companyName("Test Corp")
                .contactName("John Doe")
                .email("test@corp.tg")
                .phone("+228 90 00 00 00")
                .address("Lome, Togo")
                .active(true)
                .contacts(new java.util.ArrayList<>())
                .build();
    }

    @Nested
    @DisplayName("createClient()")
    class CreateClient {

        @Test
        @DisplayName("Cree un client quand l'email et le nom sont uniques")
        void createClient_success() {
            var request = new CreateClientRequest("New Corp", "Jane", "new@corp.tg", "+228 91 11 11 11", "Lome", null, null);
            when(clientRepository.existsByEmail("new@corp.tg")).thenReturn(false);
            when(clientRepository.existsByCompanyNameIgnoreCase("New Corp")).thenReturn(false);
            when(referenceGeneratorService.generateNextReference()).thenReturn("CLT-0002");
            when(clientRepository.save(any(Client.class))).thenReturn(client);

            var response = clientService.createClient(request, "admin-keycloak-id");

            assertNotNull(response);
            verify(clientRepository).save(any(Client.class));
        }

        @Test
        @DisplayName("Lance ConflictException si l'email existe deja")
        void createClient_duplicateEmail_throws() {
            var request = new CreateClientRequest("Corp", "J", "test@corp.tg", null, null, null, null);
            when(clientRepository.existsByEmail("test@corp.tg")).thenReturn(true);

            assertThrows(ConflictException.class,
                    () -> clientService.createClient(request, "admin"));
        }

        @Test
        @DisplayName("Lance ConflictException si le nom d'entreprise existe deja")
        void createClient_duplicateName_throws() {
            var request = new CreateClientRequest("Test Corp", "J", "other@corp.tg", null, null, null, null);
            when(clientRepository.existsByEmail("other@corp.tg")).thenReturn(false);
            when(clientRepository.existsByCompanyNameIgnoreCase("Test Corp")).thenReturn(true);

            assertThrows(ConflictException.class,
                    () -> clientService.createClient(request, "admin"));
        }
    }

    @Nested
    @DisplayName("getClient()")
    class GetClient {

        @Test
        @DisplayName("Retourne le client quand trouve")
        void getClient_found() {
            when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));

            var response = clientService.getClient(clientId);

            assertNotNull(response);
            assertEquals("Test Corp", response.companyName());
            assertEquals("CLT-0001", response.reference());
        }

        @Test
        @DisplayName("Lance NotFoundException si introuvable")
        void getClient_notFound_throws() {
            when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> clientService.getClient(clientId));
        }
    }

    @Nested
    @DisplayName("listClients()")
    class ListClients {

        @Test
        @DisplayName("Retourne une page de clients")
        void listClients_returnsPage() {
            Page<Client> page = new PageImpl<>(List.of(client));
            when(clientRepository.findByActiveTrue(any(Pageable.class))).thenReturn(page);

            var result = clientService.listClients(0, 20);

            assertNotNull(result);
            assertEquals(1, result.getContent().size());
            assertEquals("Test Corp", result.getContent().get(0).companyName());
        }
    }

    @Nested
    @DisplayName("updateClient()")
    class UpdateClient {

        @Test
        @DisplayName("Met a jour un client existant")
        void updateClient_success() {
            var request = new UpdateClientRequest("Updated Corp", "Jane", "updated@corp.tg", null, null, null, null);
            when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
            when(clientRepository.existsByEmail("updated@corp.tg")).thenReturn(false);
            when(clientRepository.existsByCompanyNameIgnoreCase("Updated Corp")).thenReturn(false);
            when(clientRepository.save(any(Client.class))).thenReturn(client);

            var response = clientService.updateClient(clientId, request);

            assertNotNull(response);
            verify(clientRepository).save(any(Client.class));
        }

        @Test
        @DisplayName("Lance NotFoundException si client introuvable")
        void updateClient_notFound_throws() {
            var request = new UpdateClientRequest("X", null, "x@x.tg", null, null, null, null);
            when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> clientService.updateClient(clientId, request));
        }
    }

    @Nested
    @DisplayName("deactivateClient()")
    class DeactivateClient {

        @Test
        @DisplayName("Desactive un client actif")
        void deactivateClient_success() {
            when(clientRepository.findById(clientId)).thenReturn(Optional.of(client));
            when(clientRepository.save(any(Client.class))).thenReturn(client);

            assertDoesNotThrow(() -> clientService.deactivateClient(clientId));
            verify(clientRepository).save(any(Client.class));
        }

        @Test
        @DisplayName("Lance NotFoundException si client introuvable")
        void deactivateClient_notFound_throws() {
            when(clientRepository.findById(clientId)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> clientService.deactivateClient(clientId));
        }
    }

    @Nested
    @DisplayName("searchClients()")
    class SearchClients {

        @Test
    @DisplayName("Recherche des clients par requete")
        void searchClients_returnsResults() {
            Page<Client> page = new PageImpl<>(List.of(client));
            when(clientRepository.search(eq("Test"), any(Pageable.class))).thenReturn(page);

            var result = clientService.searchClients("Test", 0, 20);

            assertNotNull(result);
            assertEquals(1, result.getContent().size());
        }
    }
}
