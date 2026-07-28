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

import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AnalyticsService")
class AnalyticsServiceTest {

    @Mock
    private InterventionClient interventionClient;

    @InjectMocks
    private AnalyticsService service;

    @Nested
    @DisplayName("getAnalytics()")
    class GetAnalytics {

        @Test
        @DisplayName("Calcule les statistiques correctement")
        void calculeStatistiques() {
            var interventions = List.of(
                buildDto("COMPLETED", "Climatiseur", "Client A"),
                buildDto("COMPLETED", "Climatiseur", "Client A"),
                buildDto("PENDING", "Chaudiere", "Client B")
            );
            when(interventionClient.fetchAllForReport(10_000)).thenReturn(interventions);

            var result = service.getAnalytics();

            assertEquals(3, result.totalInterventions());
            assertEquals(Map.of("COMPLETED", 2L, "PENDING", 1L), result.statusCounts());
            assertEquals(Map.of("Climatiseur", 2L, "Chaudiere", 1L), result.equipmentTypeCounts());
            assertEquals(Map.of("Client A", 2L, "Client B", 1L), result.clientCounts());
        }

        @Test
        @DisplayName("Gere les statuts null comme UNKNOWN")
        void gereStatutsNull() {
            var interventions = List.of(
                buildDto(null, null, null)
            );
            when(interventionClient.fetchAllForReport(10_000)).thenReturn(interventions);

            var result = service.getAnalytics();

            assertEquals(1, result.totalInterventions());
            assertEquals(Map.of("UNKNOWN", 1L), result.statusCounts());
            assertTrue(result.equipmentTypeCounts().isEmpty());
            assertTrue(result.clientCounts().isEmpty());
        }

        @Test
        @DisplayName("Retourne des compteurs vides si aucune intervention")
        void retourneCompteursVides() {
            when(interventionClient.fetchAllForReport(10_000)).thenReturn(List.of());

            var result = service.getAnalytics();

            assertEquals(0, result.totalInterventions());
            assertTrue(result.statusCounts().isEmpty());
            assertTrue(result.equipmentTypeCounts().isEmpty());
            assertTrue(result.clientCounts().isEmpty());
        }

        @Test
        @DisplayName("Utilise le cache pour les appels suivants")
        void utiliseCache() {
            var interventions = List.of(
                buildDto("DONE", "Type", "Client")
            );
            when(interventionClient.fetchAllForReport(10_000)).thenReturn(interventions);

            service.getAnalytics();
            service.getAnalytics();

            verify(interventionClient, times(1)).fetchAllForReport(10_000);
        }
    }

    @Nested
    @DisplayName("scheduledRefresh()")
    class ScheduledRefresh {

        @Test
        @DisplayName("Rafraichit le cache sans erreur")
        void rafraichitCacheSansErreur() {
            when(interventionClient.fetchAllForReport(10_000)).thenReturn(List.of());

            assertDoesNotThrow(() -> service.scheduledRefresh());
        }

        @Test
        @DisplayName("Ne propage pas les exceptions")
        void nePropagePasExceptions() {
            when(interventionClient.fetchAllForReport(10_000)).thenThrow(new RuntimeException("Erreur reseau"));

            assertDoesNotThrow(() -> service.scheduledRefresh());
        }
    }

    private InterventionReportDto buildDto(String status, String equipmentType, String clientName) {
        return new InterventionReportDto(
            UUID.randomUUID(), "REF-001", clientName, "email@test.com", "+228",
            equipmentType, "Brand", "Model", "Issue", "Diagnosis", "Work",
            status, null, "RESULT",
            null, null
        );
    }
}
