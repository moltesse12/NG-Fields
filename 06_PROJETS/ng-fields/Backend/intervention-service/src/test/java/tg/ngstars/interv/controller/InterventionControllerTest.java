package tg.ngstars.interv.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
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
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import tg.ngstars.interv.dto.CreateInterventionRequest;
import tg.ngstars.interv.dto.InterventionResponse;
import tg.ngstars.interv.service.InterventionService;
import tg.ngstars.interv.service.SecurityUtils;
import tg.ngstars.interv.config.SecurityConfig;

@WebMvcTest(InterventionController.class)
@Import(SecurityConfig.class)
class InterventionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().registerModule(new JavaTimeModule());

    @MockitoBean
    private InterventionService interventionService;

    @MockitoBean
    private SecurityUtils securityUtils;

    private final UUID interventionId = UUID.randomUUID();
    private final UUID clientId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        when(securityUtils.getCurrentUserId()).thenReturn(userId);
        when(securityUtils.isAdminOrManager()).thenReturn(true);
    }

    private InterventionResponse sampleResponse() {
        return new InterventionResponse(
                interventionId, "INT-0001", clientId, "ACME Inc",
                "acme@test.com", "123", "123 Main St",
                "Printer", "HP", "LaserJet", "SN123", "Office",
                "Paper jam", null, null, null, null, "SCHEDULED",
                OffsetDateTime.now(), userId, userId,
                "123 Main St", "Paris", BigDecimal.valueOf(100), null,
                null, null, null, null, null, null, null, null, null,
                null, null, null, null, null, "local-001", "Notes",
                true, OffsetDateTime.now(), OffsetDateTime.now(), List.of());
    }

    @Test
    void createIntervention_shouldReturn201() throws Exception {
        when(interventionService.createIntervention(any(), any())).thenReturn(sampleResponse());

        var request = new CreateInterventionRequest(
                "INT-0001", clientId, "ACME Inc", "acme@test.com", null, null,
                null, null, null, null, null, "Paper jam", null, null, null, null,
                "SCHEDULED", OffsetDateTime.now(), userId, "123 Main St", "Paris",
                BigDecimal.valueOf(100), null, null);

        mockMvc.perform(post("/api/interventions")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))
                        .with(authenticated()))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.reference").value("INT-0001"));
    }

    @Test
    void getInterventions_shouldReturn200() throws Exception {
        when(interventionService.getInterventions(isNull(), any(), any()))
                .thenReturn(new PageImpl<>(List.of(sampleResponse())));

        mockMvc.perform(get("/api/interventions")
                        .param("page", "0")
                        .param("size", "20")
                        .with(authenticated()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].reference").value("INT-0001"));
    }

    @Test
    void getIntervention_shouldReturn200() throws Exception {
        when(interventionService.getIntervention(eq(interventionId), any(), anyBoolean()))
                .thenReturn(sampleResponse());

        mockMvc.perform(get("/api/interventions/{id}", interventionId)
                        .with(authenticated()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference").value("INT-0001"));
    }

    @Test
    void closeIntervention_shouldReturn200() throws Exception {
        when(interventionService.closeIntervention(eq(interventionId), any(), anyBoolean()))
                .thenReturn(sampleResponse());

        mockMvc.perform(post("/api/interventions/{id}/close", interventionId)
                        .with(authenticated()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference").value("INT-0001"));
    }

    @Test
    void startIntervention_shouldReturn200() throws Exception {
        when(interventionService.startIntervention(eq(interventionId), any(), anyBoolean()))
                .thenReturn(sampleResponse());

        mockMvc.perform(post("/api/interventions/{id}/start", interventionId)
                        .with(authenticated()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference").value("INT-0001"));
    }

    @Test
    void cancelIntervention_shouldReturn200() throws Exception {
        when(interventionService.cancelIntervention(eq(interventionId), any(), anyBoolean()))
                .thenReturn(sampleResponse());

        mockMvc.perform(post("/api/interventions/{id}/cancel", interventionId)
                        .with(authenticated()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reference").value("INT-0001"));
    }

    private SecurityMockMvcRequestPostProcessors.JwtRequestPostProcessor authenticated() {
        return SecurityMockMvcRequestPostProcessors.jwt()
                .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))
                .jwt(jwt -> jwt.claim("sub", UUID.randomUUID().toString()));
    }
}
