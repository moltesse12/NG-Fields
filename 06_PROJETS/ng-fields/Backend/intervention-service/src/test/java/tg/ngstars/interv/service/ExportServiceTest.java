package tg.ngstars.interv.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tg.ngstars.interv.model.Intervention;

@DisplayName("ExportService")
class ExportServiceTest {

    private final ExportService exportService = new ExportService();
    private List<Intervention> interventions;

    @BeforeEach
    void setUp() {
        interventions = List.of(
                Intervention.builder()
                        .id(UUID.randomUUID())
                        .reference("INT-EXP-001")
                        .clientName("NG-STARs SARL")
                        .equipmentType("Serveur Dell R740")
                        .status("COMPLETED")
                        .interventionDate(OffsetDateTime.now().minusDays(3))
                        .estimatedCost(BigDecimal.valueOf(500))
                        .totalCost(BigDecimal.valueOf(450))
                        .durationMinutes(120)
                        .siteCity("Lomé")
                        .build(),
                Intervention.builder()
                        .id(UUID.randomUUID())
                        .reference("INT-EXP-002")
                        .clientName("Afrique Tech")
                        .equipmentType("Imprimante HP")
                        .status("IN_PROGRESS")
                        .estimatedCost(BigDecimal.valueOf(200))
                        .siteCity("Kara")
                        .build()
        );
    }

    @Nested
    @DisplayName("exportInterventionsCsv()")
    class CsvExport {

        @Test
        @DisplayName("Génère un CSV valide avec en-tête et données")
        void csv_validContent() throws Exception {
            var csv = exportService.exportInterventionsCsv(interventions);
            var content = new String(csv, StandardCharsets.UTF_8);

            assertTrue(content.contains("Reference,Client,Equipment,Status"));
            assertTrue(content.contains("INT-EXP-001"));
            assertTrue(content.contains("INT-EXP-002"));
            assertTrue(content.contains("NG-STARs SARL"));
            assertTrue(content.contains("Serveur Dell R740"));
            assertTrue(content.contains("Lomé"));
            assertTrue(content.contains("Kara"));
        }

        @Test
        @DisplayName("Gère les valeurs nulles")
        void csv_handlesNulls() throws Exception {
            var minimal = List.of(Intervention.builder()
                    .reference("INT-MIN")
                    .clientId(UUID.randomUUID())
                    .build());

            var csv = exportService.exportInterventionsCsv(minimal);
            var content = new String(csv, StandardCharsets.UTF_8);

            assertTrue(content.contains("INT-MIN"));
        }

        @Test
        @DisplayName("Gère les virgules dans les données (escape CSV)")
        void csv_escapesCommas() throws Exception {
            var withComma = List.of(Intervention.builder()
                    .reference("INT-COMMA")
                    .clientName("Société, Togo")
                    .clientId(UUID.randomUUID())
                    .build());

            var csv = exportService.exportInterventionsCsv(withComma);
            var content = new String(csv, StandardCharsets.UTF_8);

            assertTrue(content.contains("\"Société, Togo\""));
        }

        @Test
        @DisplayName("Retourne une liste vide")
        void csv_emptyList() throws Exception {
            var csv = exportService.exportInterventionsCsv(List.of());
            var content = new String(csv, StandardCharsets.UTF_8);

            assertTrue(content.contains("Reference"));
        }
    }

    @Nested
    @DisplayName("exportInterventionsHtml()")
    class HtmlExport {

        @Test
        @DisplayName("Génère un HTML valide")
        void html_validStructure() {
            var html = exportService.exportInterventionsHtml(interventions);
            var content = new String(html, StandardCharsets.UTF_8);

            assertTrue(content.contains("<!DOCTYPE html>"));
            assertTrue(content.contains("<table>"));
            assertTrue(content.contains("INT-EXP-001"));
            assertTrue(content.contains("NG-STARs SARL"));
            assertTrue(content.contains("</html>"));
        }

        @Test
        @DisplayName("Contient le titre du rapport")
        void html_containsTitle() {
            var html = exportService.exportInterventionsHtml(interventions);
            var content = new String(html, StandardCharsets.UTF_8);

            assertTrue(content.contains("Rapport Interventions - NG-STARs"));
        }

        @Test
        @DisplayName("Gère les caractères HTML spéciaux")
        void html_escapesHtml() {
            var withHtml = List.of(Intervention.builder()
                    .reference("INT-HTML")
                    .clientName("Société <Togo>")
                    .clientId(UUID.randomUUID())
                    .build());

            var html = exportService.exportInterventionsHtml(withHtml);
            var content = new String(html, StandardCharsets.UTF_8);

            assertTrue(content.contains("&lt;Togo&gt;"));
            assertFalse(content.contains("<Togo>"));
        }
    }
}
