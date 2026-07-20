package tg.ngstars.client.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import tg.ngstars.client.dto.ClientResponse;
import tg.ngstars.client.dto.ContactDto;
import tg.ngstars.client.dto.CreateClientRequest;
import tg.ngstars.client.dto.UpdateClientRequest;
import tg.ngstars.client.service.ClientService;
import tg.ngstars.client.service.ReferenceGeneratorService;
import tg.ngstars.client.config.SecurityConfig;

@WebMvcTest(ClientController.class)
@Import(SecurityConfig.class)
class ClientControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private ClientService clientService;

    @MockitoBean
    private ReferenceGeneratorService referenceGeneratorService;

    private final UUID clientId = UUID.randomUUID();

    private ClientResponse sampleResponse() {
        return new ClientResponse(
                clientId, "CLT-0001", "ACME Inc", "John Doe",
                "acme@test.com", "123456", "123 Main St",
                48.85, 2.35, true, OffsetDateTime.now(), List.of());
    }

    @Test
    void createClient_shouldReturn201() throws Exception {
        when(clientService.createClient(any(), any())).thenReturn(sampleResponse());

        var request = new CreateClientRequest(
                "ACME Inc", "John Doe", "acme@test.com",
                "123456", "123 Main St", 48.85, 2.35);

        mockMvc.perform(post("/api/clients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(authenticated()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.companyName").value("ACME Inc"));
    }

    @Test
    void listClients_shouldReturn200() throws Exception {
        when(clientService.listClients(0, 20))
                .thenReturn(new PageImpl<>(java.util.List.of(sampleResponse())));

        mockMvc.perform(get("/api/clients")
                        .with(authenticated()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].companyName").value("ACME Inc"));
    }

    @Test
    void getClient_shouldReturn200() throws Exception {
        when(clientService.getClient(clientId)).thenReturn(sampleResponse());

        mockMvc.perform(get("/api/clients/{id}", clientId)
                        .with(authenticated()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("ACME Inc"));
    }

    @Test
    void searchClients_shouldReturn200() throws Exception {
        when(clientService.searchClients(eq("ACME"), anyInt(), anyInt()))
                .thenReturn(new PageImpl<>(java.util.List.of(sampleResponse())));

        mockMvc.perform(get("/api/clients/search").param("q", "ACME")
                        .with(authenticated()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].companyName").value("ACME Inc"));
    }

    @Test
    void updateClient_shouldReturn200() throws Exception {
        when(clientService.updateClient(eq(clientId), any())).thenReturn(sampleResponse());

        var request = new UpdateClientRequest(
                "ACME Inc", "John Doe", "acme@test.com",
                "123456", "123 Main St", 48.85, 2.35);

        mockMvc.perform(put("/api/clients/{id}", clientId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(authenticated()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.companyName").value("ACME Inc"));
    }

    @Test
    void deactivateClient_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/clients/{id}", clientId)
                        .with(authenticated()))
                .andExpect(status().isNoContent());

        verify(clientService).deactivateClient(clientId);
    }

    @Test
    void addContact_shouldReturn201() throws Exception {
        when(clientService.addContact(any(), any())).thenReturn(
                new ContactDto(UUID.randomUUID(), "Jean Dupont", "jean@example.com", "+22890123456", "Responsable"));

        mockMvc.perform(post("/api/clients/" + UUID.randomUUID() + "/contacts")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"fullName\":\"Jean Dupont\",\"email\":\"jean@example.com\",\"phone\":\"+22890123456\",\"role\":\"Responsable\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").value("Jean Dupont"));
    }

    @Test
    void getContacts_shouldReturn200() throws Exception {
        when(clientService.getContacts(any())).thenReturn(List.of(
                new ContactDto(UUID.randomUUID(), "Jean Dupont", "jean@example.com", "+22890123456", "Responsable")));

        mockMvc.perform(get("/api/clients/" + UUID.randomUUID() + "/contacts")
                        .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fullName").value("Jean Dupont"));
    }

    @Test
    void removeContact_shouldReturn204() throws Exception {
        mockMvc.perform(delete("/api/clients/" + UUID.randomUUID() + "/contacts/" + UUID.randomUUID())
                        .with(SecurityMockMvcRequestPostProcessors.jwt().authorities(
                                new SimpleGrantedAuthority("ROLE_ADMIN"))))
                .andExpect(status().isNoContent());
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor authenticated() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                .jwt(jwt -> jwt.claim("sub", UUID.randomUUID().toString()));
    }
}
