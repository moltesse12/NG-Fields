package tg.ngstars.report.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.StreamingResponseBody;

import tg.ngstars.report.client.InterventionClient;
import tg.ngstars.report.dto.InterventionReportDto;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;

@Service
public class ReportService {

    private final InterventionClient interventionClient;

    public ReportService(InterventionClient interventionClient) {
        this.interventionClient = interventionClient;
    }

    public StreamingResponseBody exportInterventionsCsvStream() {
        return outputStream -> {
            try (var writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
                writer.write("Reference,Client,Email,Telephone,Equipement,Marque,Modele,Probleme,Diagnostic,Travail,Statut,Assignee,Resultat,Cree le,Mis a jour\r\n");

                var interventions = interventionClient.fetchAllForReport(10_000);
                for (var i : interventions) {
                    writer.write(csvLine(i));
                }
                writer.flush();
            }
        };
    }

    private String csvLine(InterventionReportDto i) {
        return escape(i.reference()) + ","
             + escape(i.clientName()) + ","
             + escape(i.clientEmail()) + ","
             + escape(i.clientPhone()) + ","
             + escape(i.equipmentType()) + ","
             + escape(i.equipmentBrand()) + ","
             + escape(i.equipmentModel()) + ","
             + escape(i.reportedIssue()) + ","
             + escape(i.diagnosis()) + ","
             + escape(i.workDone()) + ","
             + escape(i.status()) + ","
             + escape(i.assignedTo() != null ? i.assignedTo().toString() : "") + ","
             + escape(i.result()) + ","
             + escape(i.createdAt() != null ? i.createdAt().toString() : "") + ","
             + escape(i.updatedAt() != null ? i.updatedAt().toString() : "") + "\r\n";
    }

    // ponytail: prefix with tab to prevent CSV injection (CWE-1236)
    private static String escape(String val) {
        if (val == null) return "";
        if (!val.isEmpty() && (val.charAt(0) == '=' || val.charAt(0) == '+' || val.charAt(0) == '-' || val.charAt(0) == '@')) {
            val = "\t" + val;
        }
        if (val.contains(",") || val.contains("\"") || val.contains("\n")) {
            return "\"" + val.replace("\"", "\"\"") + "\"";
        }
        return val;
    }
}
