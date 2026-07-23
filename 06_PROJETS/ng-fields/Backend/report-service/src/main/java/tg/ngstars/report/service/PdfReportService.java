package tg.ngstars.report.service;

import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;
import tg.ngstars.report.client.InterventionClient;
import tg.ngstars.report.dto.InterventionReportDto;
import tg.ngstars.report.dto.PdfTemplateResponse;

import java.awt.Color;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;

@Service
public class PdfReportService {

    private static final Logger log = LoggerFactory.getLogger(PdfReportService.class);

    private final InterventionClient interventionClient;

    public PdfReportService(InterventionClient interventionClient) {
        this.interventionClient = interventionClient;
    }

    public StreamingResponseBody generateInterventionsPdf() {
        return generateInterventionsPdf(null);
    }

    public StreamingResponseBody generateInterventionsPdf(PdfTemplateResponse template) {
        return outputStream -> {
            try {
                var interventions = interventionClient.fetchAllForReport(10_000);
                var config = parseConfig(template);

                var orientation = getOrientation(config);
                var margins = getMargins(config);
                var document = new Document(orientation, margins[0], margins[1], margins[2], margins[3]);
                PdfWriter.getInstance(document, outputStream);
                document.open();

                addHeader(document, config, interventions.size());
                addTable(document, config, interventions);
                addFooter(document, config);

                document.close();
            } catch (Exception e) {
                throw new java.io.IOException("Failed to generate PDF", e);
            }
        };
    }

    private Map<String, Object> parseConfig(PdfTemplateResponse template) {
        if (template == null || template.config() == null) {
            return Map.of();
        }
        try {
            var mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            @SuppressWarnings("unchecked")
            var config = mapper.readValue(template.config(), Map.class);
            return config;
        } catch (Exception e) {
            log.warn("Failed to parse template config, using defaults", e);
            return Map.of();
        }
    }

    private com.lowagie.text.Rectangle getOrientation(Map<String, Object> config) {
        var orientation = (String) config.getOrDefault("orientation", "LANDSCAPE");
        if ("PORTRAIT".equalsIgnoreCase(orientation)) {
            return PageSize.A4;
        }
        return PageSize.A4.rotate();
    }

    private float[] getMargins(Map<String, Object> config) {
        @SuppressWarnings("unchecked")
        var margins = (Map<String, Object>) config.get("margins");
        if (margins == null) return new float[]{20, 20, 20, 20};
        return new float[]{
                getFloat(margins, "left", 20),
                getFloat(margins, "right", 20),
                getFloat(margins, "top", 20),
                getFloat(margins, "bottom", 20)
        };
    }

    private void addHeader(Document document, Map<String, Object> config, int count) throws DocumentException {
        @SuppressWarnings("unchecked")
        var header = (Map<String, Object>) config.getOrDefault("header", Map.of());
        var title = (String) config.getOrDefault("title", "Rapport des Interventions - NG-STARs");
        var titleSize = getInt(config, "titleSize", 16);
        var bgHex = (String) header.getOrDefault("backgroundColor", "#2C3E50");
        var fgHex = (String) header.getOrDefault("textColor", "#FFFFFF");

        var titleFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, titleSize, parseColor(fgHex));
        document.add(new Paragraph(title, titleFont));
        document.add(new Paragraph(" "));

        var subtitle = (String) config.getOrDefault("subtitle", "");
        if (!subtitle.isEmpty()) {
            document.add(new Paragraph(subtitle));
            document.add(new Paragraph(" "));
        }

        document.add(new Paragraph("Total: " + count + " interventions"));
        document.add(new Paragraph(" "));
    }

    @SuppressWarnings("unchecked")
    private void addTable(Document document, Map<String, Object> config, List<InterventionReportDto> interventions) throws DocumentException {
        var columns = (List<Map<String, Object>>) config.getOrDefault("columns", getDefaultColumns());
        var visibleColumns = columns.stream()
                .filter(c -> Boolean.TRUE.equals(c.getOrDefault("visible", true)))
                .toList();

        var table = new PdfPTable(visibleColumns.size());
        table.setWidthPercentage(100);

        var widths = visibleColumns.stream()
                .mapToDouble(c -> getDouble(c, "width", 10))
                .toArray();
        var widthFloats = new float[widths.length];
        for (int i = 0; i < widths.length; i++) widthFloats[i] = (float) widths[i];
        table.setWidths(widthFloats);

        @SuppressWarnings("unchecked")
        var fonts = (Map<String, Object>) config.getOrDefault("fonts", Map.of());
        var headerSize = getInt(fonts, "headerSize", 8);
        var cellSize = getInt(fonts, "cellSize", 7);

        @SuppressWarnings("unchecked")
        var headerConfig = (Map<String, Object>) config.getOrDefault("header", Map.of());
        var bgHex = (String) headerConfig.getOrDefault("backgroundColor", "#2C3E50");
        var fgHex = (String) headerConfig.getOrDefault("textColor", "#FFFFFF");

        var headerFont = FontFactory.getFont(FontFactory.HELVETICA_BOLD, headerSize, parseColor(fgHex));
        var cellFont = FontFactory.getFont(FontFactory.HELVETICA, cellSize, Color.BLACK);
        var headerBg = parseColor(bgHex);

        for (var col : visibleColumns) {
            var label = (String) col.getOrDefault("label", col.get("field"));
            var cell = new com.lowagie.text.pdf.PdfPCell(new Paragraph(label, headerFont));
            cell.setBackgroundColor(headerBg);
            cell.setHorizontalAlignment(com.lowagie.text.Element.ALIGN_CENTER);
            table.addCell(cell);
        }

        for (var intervention : interventions) {
            for (var col : visibleColumns) {
                var field = (String) col.get("field");
                var value = getFieldValue(intervention, field);
                table.addCell(new Paragraph(safe(value), cellFont));
            }
        }

        document.add(table);
    }

    private void addFooter(Document document, Map<String, Object> config) throws DocumentException {
        @SuppressWarnings("unchecked")
        var footer = (Map<String, Object>) config.getOrDefault("footer", Map.of());
        var text = (String) footer.getOrDefault("text", "");
        if (!text.isEmpty()) {
            text = text.replace("{date}", java.time.LocalDate.now().toString());
            var fontSize = getInt(footer, "fontSize", 8);
            var fgHex = (String) footer.getOrDefault("textColor", "#888888");
            var font = FontFactory.getFont(FontFactory.HELVETICA, fontSize, parseColor(fgHex));
            document.add(new Paragraph(" "));
            document.add(new Paragraph(text, font));
        }
    }

    private List<Map<String, Object>> getDefaultColumns() {
        return List.of(
            Map.of("field", "reference", "label", "Ref.", "width", 12, "visible", true),
            Map.of("field", "clientName", "label", "Client", "width", 12, "visible", true),
            Map.of("field", "clientEmail", "label", "Email", "width", 10, "visible", true),
            Map.of("field", "equipmentType", "label", "Equipement", "width", 10, "visible", true),
            Map.of("field", "equipmentBrand", "label", "Marque", "width", 10, "visible", true),
            Map.of("field", "reportedIssue", "label", "Probleme", "width", 12, "visible", true),
            Map.of("field", "diagnosis", "label", "Diagnostic", "width", 12, "visible", true),
            Map.of("field", "workDone", "label", "Travail", "width", 12, "visible", true),
            Map.of("field", "status", "label", "Statut", "width", 10, "visible", true)
        );
    }

    private String getFieldValue(InterventionReportDto i, String field) {
        return switch (field) {
            case "reference" -> i.reference();
            case "clientName" -> i.clientName();
            case "clientEmail" -> i.clientEmail();
            case "clientPhone" -> i.clientPhone();
            case "equipmentType" -> i.equipmentType();
            case "equipmentBrand" -> i.equipmentBrand();
            case "equipmentModel" -> i.equipmentModel();
            case "reportedIssue" -> i.reportedIssue();
            case "diagnosis" -> i.diagnosis();
            case "workDone" -> i.workDone();
            case "status" -> i.status();
            case "result" -> i.result();
            default -> null;
        };
    }

    private static String safe(String val) {
        if (val == null) return "";
        return val.length() > 50 ? val.substring(0, 47) + "..." : val;
    }

    private Color parseColor(String hex) {
        if (hex == null || hex.length() != 7) return Color.BLACK;
        try {
            var r = Integer.parseInt(hex.substring(1, 3), 16);
            var g = Integer.parseInt(hex.substring(3, 5), 16);
            var b = Integer.parseInt(hex.substring(5, 7), 16);
            return new Color(r, g, b);
        } catch (Exception e) {
            return Color.BLACK;
        }
    }

    private int getInt(Map<String, Object> map, String key, int defaultVal) {
        var val = map.get(key);
        if (val instanceof Number n) return n.intValue();
        return defaultVal;
    }

    private double getDouble(Map<String, Object> map, String key, double defaultVal) {
        var val = map.get(key);
        if (val instanceof Number n) return n.doubleValue();
        return defaultVal;
    }

    private float getFloat(Map<String, Object> map, String key, float defaultVal) {
        var val = map.get(key);
        if (val instanceof Number n) return n.floatValue();
        return defaultVal;
    }
}
