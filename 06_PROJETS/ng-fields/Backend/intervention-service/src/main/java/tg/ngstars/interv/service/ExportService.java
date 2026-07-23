package tg.ngstars.interv.service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import tg.ngstars.interv.model.Intervention;

@Service
public class ExportService {

    private static final Logger log = LoggerFactory.getLogger(ExportService.class);

    public byte[] exportInterventionsCsv(List<Intervention> interventions) throws IOException {
        var baos = new ByteArrayOutputStream();
        var writer = new OutputStreamWriter(baos, StandardCharsets.UTF_8);
        var pw = new PrintWriter(writer);

        pw.println("Reference,Client,Equipment,Status,Date,Technician,Estimated Cost,Total Cost,Duration (min),Site City");

        for (var i : interventions) {
            pw.println(String.join(",",
                    escape(i.getReference()),
                    escape(i.getClientName()),
                    escape(i.getEquipmentType()),
                    escape(i.getStatus()),
                    escape(i.getInterventionDate() != null ? i.getInterventionDate().toString() : ""),
                    escape(i.getAssignedTo() != null ? i.getAssignedTo().toString() : ""),
                    escape(i.getEstimatedCost() != null ? i.getEstimatedCost().toString() : "0"),
                    escape(i.getTotalCost() != null ? i.getTotalCost().toString() : "0"),
                    escape(i.getDurationMinutes() != null ? i.getDurationMinutes().toString() : ""),
                    escape(i.getSiteCity() != null ? i.getSiteCity() : "")));
        }

        pw.flush();
        log.info("Export CSV genere : {} interventions", interventions.size());
        return baos.toByteArray();
    }

    public byte[] exportInterventionsHtml(List<Intervention> interventions) {
        var sb = new StringBuilder();
        sb.append("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        sb.append("<title>Export Interventions - NG-STARs</title>");
        sb.append("<style>body{font-family:Arial,sans-serif;margin:20px}table{border-collapse:collapse;width:100%}th,td{border:1px solid #ddd;padding:8px;text-align:left}th{background:#f4f4f4}tr:nth-child(even){background:#fafafa}</style>");
        sb.append("</head><body>");
        sb.append("<h1>Rapport Interventions - NG-STARs</h1>");
        sb.append("<p>Genere le : ").append(java.time.LocalDateTime.now()).append("</p>");
        sb.append("<table><thead><tr>");
        sb.append("<th>Reference</th><th>Client</th><th>Equipement</th><th>Statut</th><th>Date</th><th>Cout estime</th><th>Cout total</th><th>Duree (min)</th><th>Ville</th>");
        sb.append("</tr></thead><tbody>");

        for (var i : interventions) {
            sb.append("<tr>");
            sb.append("<td>").append(escapeHtml(i.getReference())).append("</td>");
            sb.append("<td>").append(escapeHtml(i.getClientName())).append("</td>");
            sb.append("<td>").append(escapeHtml(i.getEquipmentType())).append("</td>");
            sb.append("<td>").append(escapeHtml(i.getStatus())).append("</td>");
            sb.append("<td>").append(i.getInterventionDate() != null ? i.getInterventionDate().toLocalDate() : "-").append("</td>");
            sb.append("<td>").append(i.getEstimatedCost() != null ? i.getEstimatedCost() : "0").append("</td>");
            sb.append("<td>").append(i.getTotalCost() != null ? i.getTotalCost() : "0").append("</td>");
            sb.append("<td>").append(i.getDurationMinutes() != null ? i.getDurationMinutes() : "-").append("</td>");
            sb.append("<td>").append(escapeHtml(i.getSiteCity() != null ? i.getSiteCity() : "")).append("</td>");
            sb.append("</tr>");
        }

        sb.append("</tbody></table></body></html>");
        log.info("Export HTML genere : {} interventions", interventions.size());
        return sb.toString().getBytes(StandardCharsets.UTF_8);
    }

    private String escape(String value) {
        if (value == null) return "";
        if (value.contains(",") || value.contains("\"") || value.contains("\n")) {
            return "\"" + value.replace("\"", "\"\"") + "\"";
        }
        return value;
    }

    private String escapeHtml(String value) {
        if (value == null) return "";
        return value.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }
}
