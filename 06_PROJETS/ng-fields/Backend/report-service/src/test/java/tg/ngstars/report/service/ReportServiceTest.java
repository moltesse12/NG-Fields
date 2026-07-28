package tg.ngstars.report.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import tg.ngstars.report.client.InterventionClient;
import tg.ngstars.report.dto.InterventionReportDto;

import java.io.ByteArrayOutputStream;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("ReportService")
class ReportServiceTest {

    @Mock
    private InterventionClient interventionClient;

    @InjectMocks
    private ReportService reportService;

    @Nested
    @DisplayName("exportInterventionsCsvStream()")
    class ExportCsv {

        @Test
        @DisplayName("Genere un CSV avec l'en-tete et les donnees")
        void genereCsvAvecEnTeteEtDonnees() throws Exception {
            var intervention = new InterventionReportDto(
                UUID.randomUUID(), "INT-001", "Client Test", "client@test.com", "+228 90 00 00 00",
                "Climatiseur", "Daikin", "FTXB35", "Pas de froid", "Compresseur HS", "Remplacement compresseur",
                "COMPLETED", UUID.randomUUID(), "REPARATION",
                OffsetDateTime.now(), OffsetDateTime.now()
            );
            when(interventionClient.fetchAllForReport(10_000)).thenReturn(List.of(intervention));

            var outputStream = new ByteArrayOutputStream();
            reportService.exportInterventionsCsvStream().writeTo(outputStream);

            var csv = outputStream.toString("UTF-8");
            assertTrue(csv.startsWith("Reference,Client,Email,Telephone,Equipement,Marque,Modele,Probleme,Diagnostic,Travail,Statut,Assignee,Resultat,Cree le,Mis a jour\r\n"));
            assertTrue(csv.contains("INT-001"));
            assertTrue(csv.contains("Client Test"));
        }

        @Test
        @DisplayName("Retourne un CSV avec juste l'en-tete si aucune intervention")
        void csvVideSiAucuneIntervention() throws Exception {
            when(interventionClient.fetchAllForReport(10_000)).thenReturn(List.of());

            var outputStream = new ByteArrayOutputStream();
            reportService.exportInterventionsCsvStream().writeTo(outputStream);

            var csv = outputStream.toString("UTF-8");
            var lines = csv.split("\r\n");
            assertEquals(1, lines.length);
            assertTrue(lines[0].startsWith("Reference,Client,"));
        }

        @Test
        @DisplayName("Protege contre l'injection CSV (CWE-1236)")
        void protegeContreInjectionCSV() throws Exception {
            var intervention = new InterventionReportDto(
                UUID.randomUUID(), "=CMD('calc')", "Client", "email@test.com", "+228",
                "Type", "Brand", "Model", "Issue", "Diagnosis", "Work",
                "DONE", null, "RESULT",
                null, null
            );
            when(interventionClient.fetchAllForReport(10_000)).thenReturn(List.of(intervention));

            var outputStream = new ByteArrayOutputStream();
            reportService.exportInterventionsCsvStream().writeTo(outputStream);

            var csv = outputStream.toString("UTF-8");
            assertTrue(csv.contains("\t=CMD('calc')"));
            assertFalse(csv.contains("\n=CMD('calc')"));
        }

        @Test
        @DisplayName("Echappe les virgules et guillemets dans les valeurs")
        void echappeVirgulesEtGuillemets() throws Exception {
            var intervention = new InterventionReportDto(
                UUID.randomUUID(), "REF-001", "Client, Inc.", "email@test.com", "+228",
                "Type", "Brand", "Model", "Issue with \"quotes\"", "Diagnosis", "Work",
                "DONE", null, "RESULT",
                null, null
            );
            when(interventionClient.fetchAllForReport(10_000)).thenReturn(List.of(intervention));

            var outputStream = new ByteArrayOutputStream();
            reportService.exportInterventionsCsvStream().writeTo(outputStream);

            var csv = outputStream.toString("UTF-8");
            assertTrue(csv.contains("\"Client, Inc.\""));
            assertTrue(csv.contains("\"Issue with \"\"quotes\"\"\""));
        }
    }
}
