package tg.ngstars.client.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import tg.ngstars.client.dto.*;
import tg.ngstars.client.service.ClientService;

@WebMvcTest(ClientController.class)
@DisplayName("ClientController - Tests d'integration WebMvc")
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ClientService clientService;

    private UUID clientId;
    private ClientResponse clientResponse;
    private Jwt adminJwt;

    @BeforeEach
    void setUp() {
        clientId = UUID.randomUUID();
        clientResponse = new ClientResponse(
                clientId, "CLT-0001", "Test Corp", "John Doe",
                "test@corp.tg", "+22890000000", "Lome, Togo",
                6.13, 1.22, true, OffsetDateTime.now(), List.of());

        adminJwt = Jwt.withTokenValue("fake-token")
                .subject(UUID.randomUUID().toString())
                .claim("realm_access", Map.of("roles", java.util.List.of("ADMIN")))
                .header("alg", "RS256")
                .build();
    }

    @Nested
    @DisplayName("POST /api/clients")
    class CreateClient {
        @Test
        @DisplayName("Cree un client avec le role ADMIN")
        void createClient_asAdmin_returns201() throws Exception {
            var request = new CreateClientRequest("New Corp", "Jane",
                    "new@corp.tg", "+22891111111", "Lome", null, null);
            when(clientService.createClient(any(), anyString())).thenReturn(clientResponse);

            mockMvc.perform(post("/api/clients")
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                                    .jwt(j -> j.subject(UUID.randomUUID().toString())))
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.reference").value("CLT-0001"));
        }

        @Test
        @DisplayName("Rejette si non ADMIN")
        void createClient_asTechnician_returns403() throws Exception {
            var request = new CreateClientRequest("New Corp", "Jane",
                    "new@corp.tg", "+22891111111", "Lome", null, null);

            mockMvc.perform(post("/api/clients")
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_TECHNICIAN"))
                                    .jwt(j -> j.subject(UUID.randomUUID().toString())))
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/clients")
    class ListClients {
        @Test
        @DisplayName("Liste les clients avec le role ADMIN")
        void listClients_asAdmin_returns200() throws Exception {
            when(clientService.listClients(anyInt(), anyInt()))
                    .thenReturn(new PageImpl<>(List.of(clientResponse)));

            mockMvc.perform(get("/api/clients")
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].reference").value("CLT-0001"));
        }

        @Test
        @DisplayName("Liste les clients avec le role TECHNICIAN")
        void listClients_asTechnician_returns200() throws Exception {
            when(clientService.listClients(anyInt(), anyInt()))
                    .thenReturn(new PageImpl<>(List.of(clientResponse)));

            mockMvc.perform(get("/api/clients")
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_TECHNICIAN"))))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Rejette si non AUTH")
        void listClients_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/clients"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("GET /api/clients/{id}")
    class GetClient {
        @Test
        @DisplayName("Obtient un client par ID")
        void getClient_asAdmin_returns200() throws Exception {
            when(clientService.getClient(clientId)).thenReturn(clientResponse);

            mockMvc.perform(get("/api/clients/" + clientId)
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.companyName").value("Test Corp"));
        }
    }

    @Nested
    @DisplayName("PUT /api/clients/{id}")
    class UpdateClient {
        @Test
        @DisplayName("Met a jour un client avec ADMIN")
        void updateClient_asAdmin_returns200() throws Exception {
            var request = new UpdateClientRequest("Updated Corp", "Jane",
                    "updated@corp.tg", "+22892222222", "Kara", null, null);
            when(clientService.updateClient(any(), any())).thenReturn(clientResponse);

            mockMvc.perform(put("/api/clients/" + clientId)
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Rejette si non ADMIN")
        void updateClient_asManager_returns403() throws Exception {
            var request = new UpdateClientRequest("Updated Corp", "Jane",
                    "updated@corp.tg", "+22892222222", "Kara", null, null);

            mockMvc.perform(put("/api/clients/" + clientId)
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_MANAGER")))
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("DELETE /api/clients/{id}")
    class DeactivateClient {
        @Test
        @DisplayName("Desactive un client avec ADMIN")
        void deactivateClient_asAdmin_returns204() throws Exception {
            doNothing().when(clientService).deactivateClient(any());

            mockMvc.perform(delete("/api/clients/" + clientId)
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                            .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(status().isNoContent());
        }

        @Test
        @DisplayName("Rejette si TECHNICIAN")
        void deactivateClient_asTechnician_returns403() throws Exception {
            mockMvc.perform(delete("/api/clients/" + clientId)
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_TECHNICIAN")))
                            .with(SecurityMockMvcRequestPostProcessors.csrf()))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/clients/search")
    class SearchClients {
        @Test
        @DisplayName("Recherche des clients")
        void searchClients_asManager_returns200() throws Exception {
            when(clientService.searchClients(eq("Test"), anyInt(), anyInt()))
                    .thenReturn(new PageImpl<>(List.of(clientResponse)));

            mockMvc.perform(get("/api/clients/search")
                            .param("q", "Test")
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_MANAGER"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.content[0].reference").value("CLT-0001"));
        }
    }

    @Nested
    @DisplayName("POST /api/clients/{id}/contacts")
    class AddContact {
        @Test
        @DisplayName("Ajoute un contact avec ADMIN")
        void addContact_asAdmin_returns201() throws Exception {
            var request = new CreateContactRequest("Contact Name",
                    "contact@corp.tg", "+22893333333", "Manager");
            var contactDto = new ContactDto(UUID.randomUUID(), "Contact Name",
                    "contact@corp.tg", "+22893333333", "Manager");
            when(clientService.addContact(any(), any())).thenReturn(contactDto);

            mockMvc.perform(post("/api/clients/" + clientId + "/contacts")
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                            .with(SecurityMockMvcRequestPostProcessors.csrf())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(request)))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.fullName").value("Contact Name"));
        }
    }
}
