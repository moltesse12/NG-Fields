package tg.ngstars.report.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import tg.ngstars.report.client.InterventionClient;
import tg.ngstars.report.dto.AnalyticsDto;
import tg.ngstars.report.dto.InterventionReportDto;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock InterventionClient interventionClient;
    ReportService reportService;
    AnalyticsService analyticsService;
    PdfReportService pdfReportService;

    List<InterventionReportDto> sampleData;

    @BeforeEach
    void setUp() {
        reportService = new ReportService(interventionClient);
        analyticsService = new AnalyticsService(interventionClient);
        pdfReportService = new PdfReportService(interventionClient);

        sampleData = List.of(
                new InterventionReportDto(UUID.randomUUID(), "INT-001", "ACME", "acme@test.com", "123",
                        "Clim", "Daikin", "DX200", "Bruit", "Compresseur", "Remplacement", "COMPLETED",
                        UUID.randomUUID(), "RESOLU", true, new BigDecimal("1500.00"),
                        OffsetDateTime.now().minusDays(5), OffsetDateTime.now()),
                new InterventionReportDto(UUID.randomUUID(), "INT-002", "BetaCo", "beta@test.com", "456",
                        "Frigo", "Samsung", "RF300", "Fuite", "Joint", "Changement", "IN_PROGRESS",
                        UUID.randomUUID(), null, true, new BigDecimal("800.00"),
                        OffsetDateTime.now().minusDays(3), OffsetDateTime.now()),
                new InterventionReportDto(UUID.randomUUID(), "INT-003", "ACME", "acme@test.com", "123",
                        "Clim", "Bosch", "OV500", "Ne chauffe pas", "Thermostat", "Remplacement", "PENDING",
                        null, null, false, null,
                        OffsetDateTime.now().minusDays(1), OffsetDateTime.now())
        );
    }

    @Test
    void exportCsv_shouldWriteHeaderAndRows() throws Exception {
        when(interventionClient.fetchAllForReport(10_000)).thenReturn(sampleData);

        StreamingResponseBody stream = reportService.exportInterventionsCsvStream();
        var baos = new ByteArrayOutputStream();
        stream.writeTo(baos);
        var csv = baos.toString("UTF-8");

        assertTrue(csv.contains("Reference,Client,Email"));
        assertTrue(csv.contains("INT-001"));
        assertTrue(csv.contains("INT-002"));
        assertTrue(csv.contains("INT-003"));
        assertTrue(csv.contains("ACME"));
    }

    @Test
    void exportCsv_emptyData_shouldWriteHeaderOnly() throws Exception {
        when(interventionClient.fetchAllForReport(10_000)).thenReturn(List.of());

        StreamingResponseBody stream = reportService.exportInterventionsCsvStream();
        var baos = new ByteArrayOutputStream();
        stream.writeTo(baos);
        var csv = baos.toString("UTF-8");

        assertTrue(csv.contains("Reference,Client,Email"));
        assertEquals(1, csv.lines().count());
    }

    @Test
    void exportPdf_shouldGeneratePdf() throws Exception {
        when(interventionClient.fetchAllForReport(10_000)).thenReturn(sampleData);

        StreamingResponseBody stream = pdfReportService.generateInterventionsPdf();
        var baos = new ByteArrayOutputStream();
        stream.writeTo(baos);
        var bytes = baos.toByteArray();

        assertTrue(bytes.length > 0);
        assertEquals(0x25, bytes[0] & 0xFF);
        assertEquals(0x50, bytes[1] & 0xFF);
    }

    @Test
    void analytics_shouldComputeCorrectly() {
        when(interventionClient.fetchAllForReport(10_000)).thenReturn(sampleData);

        AnalyticsDto analytics = analyticsService.getAnalytics();

        assertEquals(3, analytics.totalInterventions());
        assertEquals(1L, analytics.statusCounts().get("COMPLETED"));
        assertEquals(1L, analytics.statusCounts().get("IN_PROGRESS"));
        assertEquals(1L, analytics.statusCounts().get("PENDING"));
        assertEquals(2, analytics.billableCount());
        assertEquals(1, analytics.nonBillableCount());
        assertEquals(new BigDecimal("2300.00"), analytics.totalBillingAmount());
        assertEquals(2L, analytics.equipmentTypeCounts().get("Clim"));
        assertEquals(2L, analytics.clientCounts().get("ACME"));
    }

    @Test
    void analytics_emptyData_shouldReturnZeros() {
        when(interventionClient.fetchAllForReport(10_000)).thenReturn(List.of());

        AnalyticsDto analytics = analyticsService.getAnalytics();

        assertEquals(0, analytics.totalInterventions());
        assertEquals(0, analytics.billableCount());
        assertEquals(0, analytics.nonBillableCount());
        assertEquals(BigDecimal.ZERO, analytics.totalBillingAmount());
    }
}
