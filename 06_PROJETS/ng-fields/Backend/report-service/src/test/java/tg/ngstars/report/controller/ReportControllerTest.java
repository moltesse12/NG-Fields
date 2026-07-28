package tg.ngstars.report.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Map;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import com.fasterxml.jackson.databind.ObjectMapper;

import tg.ngstars.report.dto.AnalyticsDto;
import tg.ngstars.report.service.AnalyticsService;
import tg.ngstars.report.service.PdfReportService;
import tg.ngstars.report.service.PdfTemplateService;
import tg.ngstars.report.service.ReportService;

@WebMvcTest(ReportController.class)
@DisplayName("ReportController - Tests d'integration WebMvc")
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReportService reportService;

    @MockitoBean
    private AnalyticsService analyticsService;

    @MockitoBean
    private PdfReportService pdfReportService;

    @MockitoBean
    private PdfTemplateService templateService;

    @BeforeEach
    void setUp() {
    }

    @Nested
    @DisplayName("GET /api/reports/analytics")
    class GetAnalytics {
        @Test
        @DisplayName("Retourne les analytics avec ADMIN")
        void getAnalytics_asAdmin_returns200() throws Exception {
            var analytics = new AnalyticsDto(10L,
                    Map.of("COMPLETED", 5L, "PENDING", 5L),
                    Map.of("Serveur", 3L, "Imprimante", 7L),
                    Map.of("Client A", 4L, "Client B", 6L));
            when(analyticsService.getAnalytics()).thenReturn(analytics);

            mockMvc.perform(get("/api/reports/analytics")
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.totalInterventions").value(10));
        }

        @Test
        @DisplayName("Retourne les analytics avec MANAGER")
        void getAnalytics_asManager_returns200() throws Exception {
            var analytics = new AnalyticsDto(5L, Map.of(), Map.of(), Map.of());
            when(analyticsService.getAnalytics()).thenReturn(analytics);

            mockMvc.perform(get("/api/reports/analytics")
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_MANAGER"))))
                    .andExpect(status().isOk());
        }

        @Test
        @DisplayName("Rejette si TECHNICIAN")
        void getAnalytics_asTechnician_returns403() throws Exception {
            mockMvc.perform(get("/api/reports/analytics")
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_TECHNICIAN"))))
                    .andExpect(status().isForbidden());
        }

        @Test
        @DisplayName("Rejette si non authentifie")
        void getAnalytics_noAuth_returns401() throws Exception {
            mockMvc.perform(get("/api/reports/analytics"))
                    .andExpect(status().isUnauthorized());
        }
    }

    @Nested
    @DisplayName("GET /api/reports/interventions/csv")
    class ExportCsv {
        @Test
        @DisplayName("Exporte le CSV avec ADMIN")
        void exportCsv_asAdmin_returns200() throws Exception {
            StreamingResponseBody stream = outputStream -> {
                outputStream.write("Reference,Client\n".getBytes());
                outputStream.write("INT-001,Client A\n".getBytes());
            };
            when(reportService.exportInterventionsCsvStream()).thenReturn(stream);

            mockMvc.perform(get("/api/reports/interventions/csv")
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Disposition",
                            "attachment; filename=interventions.csv"));
        }

        @Test
        @DisplayName("Rejette si TECHNICIAN")
        void exportCsv_asTechnician_returns403() throws Exception {
            mockMvc.perform(get("/api/reports/interventions/csv")
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_TECHNICIAN"))))
                    .andExpect(status().isForbidden());
        }
    }

    @Nested
    @DisplayName("GET /api/reports/interventions/pdf")
    class ExportPdf {
        @Test
        @DisplayName("Exporte le PDF avec ADMIN")
        void exportPdf_asAdmin_returns200() throws Exception {
            StreamingResponseBody stream = outputStream -> outputStream.write(new byte[0]);
            when(pdfReportService.generateInterventionsPdf(isNull())).thenReturn(stream);

            mockMvc.perform(get("/api/reports/interventions/pdf")
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().isOk())
                    .andExpect(header().string("Content-Disposition",
                            "attachment; filename=interventions.pdf"))
                    .andExpect(header().string("Content-Type", "application/pdf"));
        }

        @Test
        @DisplayName("Exporte le PDF avec un templateId")
        void exportPdf_withTemplateId_returns200() throws Exception {
            var templateId = UUID.randomUUID();
            StreamingResponseBody stream = outputStream -> outputStream.write(new byte[0]);
            when(pdfReportService.generateInterventionsPdf(any())).thenReturn(stream);

            mockMvc.perform(get("/api/reports/interventions/pdf")
                            .param("templateId", templateId.toString())
                            .with(SecurityMockMvcRequestPostProcessors.jwt()
                                    .authorities(new SimpleGrantedAuthority("ROLE_ADMIN"))))
                    .andExpect(status().isOk());
        }
    }
}
