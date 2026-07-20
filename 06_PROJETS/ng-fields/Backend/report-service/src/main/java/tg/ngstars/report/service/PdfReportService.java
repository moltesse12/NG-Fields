package tg.ngstars.report.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import tg.ngstars.report.client.InterventionClient;

import java.awt.Color;
import java.io.OutputStream;

@Service
public class PdfReportService {

    private final InterventionClient interventionClient;

    public PdfReportService(InterventionClient interventionClient) {
        this.interventionClient = interventionClient;
    }

    public StreamingResponseBody generateInterventionsPdf() {
        return outputStream -> {
            var interventions = interventionClient.fetchAllForReport(10_000);
            var document = new Document(PageSize.A4.rotate(), 20, 20, 20, 20);
            PdfWriter.getInstance(document, outputStream);
            document.open();

            var titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16, Color.BLACK);
            document.add(new Paragraph("Rapport des Interventions - NG-STARs", titleFont));
            document.add(new Paragraph(" "));
            document.add(new Paragraph("Total: " + interventions.size() + " interventions"));
            document.add(new Paragraph(" "));

            var table = new PdfPTable(10);
            table.setWidthPercentage(100);
            table.setWidths(new float[]{12, 12, 10, 10, 10, 12, 12, 12, 10, 10});

            var headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 8, Color.WHITE);
            var cellFont = FontFactory.getFont(FontFactory.HELVETICA, 7, Color.BLACK);
            var headerBg = new Color(44, 62, 80);

            String[] headers = {"Réf.", "Client", "Email", "Équipement", "Marque", "Problème", "Diagnostic", "Travail", "Statut", "Facturable"};
            for (var header : headers) {
                var cell = new com.lowagie.text.pdf.PdfPCell(new Paragraph(header, headerFont));
                cell.setBackgroundColor(headerBg);
                cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
                table.addCell(cell);
            }

            for (var i : interventions) {
                table.addCell(new Paragraph(safe(i.reference()), cellFont));
                table.addCell(new Paragraph(safe(i.clientName()), cellFont));
                table.addCell(new Paragraph(safe(i.clientEmail()), cellFont));
                table.addCell(new Paragraph(safe(i.equipmentType()), cellFont));
                table.addCell(new Paragraph(safe(i.equipmentBrand()), cellFont));
                table.addCell(new Paragraph(safe(i.reportedIssue()), cellFont));
                table.addCell(new Paragraph(safe(i.diagnosis()), cellFont));
                table.addCell(new Paragraph(safe(i.workDone()), cellFont));
                table.addCell(new Paragraph(safe(i.status()), cellFont));
                table.addCell(new Paragraph(Boolean.TRUE.equals(i.billable()) ? "Oui" : "Non", cellFont));
            }

            document.add(table);
            document.close();
        };
    }

    private static String safe(String val) {
        if (val == null) return "";
        return val.length() > 50 ? val.substring(0, 47) + "..." : val;
    }
}
