package tg.ngstars.interv.service;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;

import org.openpdf.text.Document;
import org.openpdf.text.FontFactory;
import org.openpdf.text.Paragraph;
import org.openpdf.text.pdf.PdfPTable;
import org.openpdf.text.pdf.PdfWriter;

import tg.ngstars.interv.model.Intervention;
import tg.ngstars.interv.model.InterventionItem;

public final class PdfService {

    private PdfService() {}

    public static void write(Intervention intervention, OutputStream out) {
        var document = new Document();
        try {
            PdfWriter.getInstance(document, out);
            document.open();

            var titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18);
            var headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12);
            var normalFont = FontFactory.getFont(FontFactory.HELVETICA, 10);

            document.add(new Paragraph("Intervention Report", titleFont));
            document.add(new Paragraph("Reference: " + intervention.getReference(), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Client Information", headerFont));
            document.add(new Paragraph("Name: " + intervention.getClientName(), normalFont));
            document.add(new Paragraph("Email: " + intervention.getClientEmail(), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Equipment", headerFont));
            document.add(new Paragraph("Type: " + intervention.getEquipmentType(), normalFont));
            document.add(new Paragraph("Brand: " + intervention.getEquipmentBrand(), normalFont));
            document.add(new Paragraph("Model: " + intervention.getEquipmentModel(), normalFont));
            document.add(new Paragraph("Serial: " + intervention.getEquipmentSerial(), normalFont));
            document.add(new Paragraph(" "));

            document.add(new Paragraph("Issue & Diagnosis", headerFont));
            document.add(new Paragraph("Reported: " + intervention.getReportedIssue(), normalFont));
            document.add(new Paragraph("Diagnosis: " + intervention.getDiagnosis(), normalFont));
            document.add(new Paragraph(" "));

            if (intervention.getItems() != null && !intervention.getItems().isEmpty()) {
                document.add(new Paragraph("Items", headerFont));
                var table = new PdfPTable(5);
                table.setWidthPercentage(100);
                table.addCell("Type");
                table.addCell("Description");
                table.addCell("Qty");
                table.addCell("Unit Price");
                table.addCell("Total");
                for (InterventionItem item : intervention.getItems()) {
                    table.addCell(item.getType());
                    table.addCell(item.getDescription());
                    table.addCell(String.valueOf(item.getQuantity()));
                    table.addCell(item.getUnitPrice().toString());
                    table.addCell(item.getTotal().toString());
                }
                document.add(table);
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate PDF", e);
        } finally {
            document.close();
        }
    }

    public static byte[] generate(Intervention intervention) {
        var baos = new ByteArrayOutputStream();
        write(intervention, baos);
        return baos.toByteArray();
    }
}
