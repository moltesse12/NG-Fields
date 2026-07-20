package tg.ngstars.report.controller;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import tg.ngstars.report.dto.AnalyticsDto;
import tg.ngstars.report.service.AnalyticsService;
import tg.ngstars.report.service.PdfReportService;
import tg.ngstars.report.service.ReportService;

@RestController
@RequestMapping("/api/reports")
@PreAuthorize("hasAnyRole('ADMIN', 'MANAGER')")
public class ReportController {

    private final ReportService reportService;
    private final AnalyticsService analyticsService;
    private final PdfReportService pdfReportService;

    public ReportController(ReportService reportService, AnalyticsService analyticsService, PdfReportService pdfReportService) {
        this.reportService = reportService;
        this.analyticsService = analyticsService;
        this.pdfReportService = pdfReportService;
    }

    @GetMapping(value = "/interventions/csv", produces = "text/csv")
    public ResponseEntity<StreamingResponseBody> exportInterventionsCsv() {
        var stream = reportService.exportInterventionsCsvStream();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=interventions.csv")
                .contentType(MediaType.parseMediaType("text/csv; charset=UTF-8"))
                .body(stream);
    }

    @GetMapping(value = "/interventions/pdf", produces = "application/pdf")
    public ResponseEntity<StreamingResponseBody> exportInterventionsPdf() {
        var stream = pdfReportService.generateInterventionsPdf();
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=interventions.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(stream);
    }

    @GetMapping(value = "/analytics", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AnalyticsDto> getAnalytics() {
        return ResponseEntity.ok(analyticsService.getAnalytics());
    }
}
