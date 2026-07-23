package tg.ngstars.interv.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;

import tg.ngstars.common.exception.NotFoundException;
import tg.ngstars.interv.dto.CreateInterventionRequest;
import tg.ngstars.interv.dto.CreateInterventionRequest.CreateItemRequest;
import tg.ngstars.interv.dto.InterventionStatsResponse;
import tg.ngstars.interv.dto.UpdateDiagnosisRequest;
import tg.ngstars.interv.dto.UpdateEquipmentRequest;
import tg.ngstars.interv.dto.UpdateInterventionRequest;
import tg.ngstars.interv.dto.UpdateRecommendationsRequest;
import tg.ngstars.interv.dto.UpdateResultRequest;
import tg.ngstars.interv.dto.UpdateScheduleRequest;
import tg.ngstars.interv.model.Intervention;
import tg.ngstars.interv.model.InterventionItem;
import tg.ngstars.interv.repository.InterventionRepository;

@ExtendWith(MockitoExtension.class)
@DisplayName("InterventionService")
class InterventionServiceTest {

    @Mock
    private InterventionRepository interventionRepository;
    @Mock
    private InterventionStatusService statusService;
    @Mock
    private InterventionEmailService emailService;
    @Mock
    private SseEmitterManager sseManager;

    @InjectMocks
    private InterventionService interventionService;

    private UUID userId;
    private UUID clientId;
    private UUID interventionId;
    private Intervention intervention;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        clientId = UUID.randomUUID();
        interventionId = UUID.randomUUID();

        intervention = Intervention.builder()
                .id(interventionId)
                .reference("INT-TEST-001")
                .clientId(clientId)
                .clientName("Test Client")
                .clientEmail("test@ngstars.tg")
                .equipmentType("Serveur Dell")
                .siteAddress("Lomé, Togo")
                .status("PENDING")
                .createdBy(userId)
                .active(true)
                .items(new java.util.ArrayList<>())
                .build();
    }

    @Nested
    @DisplayName("createIntervention()")
    class CreateIntervention {

        @Test
        @DisplayName("Crée une intervention avec succès")
        void createIntervention_success() {
            var request = new CreateInterventionRequest(
                    "INT-NEW-001", clientId, "NG-STARs", "ng@stars.tg",
                    "+22890111111", "Lomé", "Imprimante", "HP", "LaserJet",
                    "SN123", "Bureau", "Papier coincé", null, null,
                    null, OffsetDateTime.now(), null, "Lomé", "Lomé",
                    BigDecimal.valueOf(150), null, null);

            when(interventionRepository.existsByReference("INT-NEW-001")).thenReturn(false);
            when(interventionRepository.save(any(Intervention.class))).thenAnswer(inv -> {
                var i = inv.getArgument(0, Intervention.class);
                if (i.getId() == null) i.setId(UUID.randomUUID());
                return i;
            });

            var response = interventionService.createIntervention(request, userId);

            assertNotNull(response);
            assertEquals("INT-NEW-001", response.reference());
            assertEquals(clientId, response.clientId());
            verify(interventionRepository).save(any(Intervention.class));
            verify(sseManager).sendEvent(eq("INTERVENTION_CREATED"), any());
        }

        @Test
        @DisplayName("Rejette si la référence existe déjà")
        void createIntervention_duplicateReference_throws() {
            var request = new CreateInterventionRequest(
                    "INT-DUP-001", clientId, "NG-STARs", null,
                    null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, null, null, null,
                    null, null, null);

            when(interventionRepository.existsByReference("INT-DUP-001")).thenReturn(true);

            assertThrows(IllegalArgumentException.class,
                    () -> interventionService.createIntervention(request, userId));
        }

        @Test
        @DisplayName("Crée avec items")
        void createIntervention_withItems() {
            var itemReq = new CreateItemRequest("PART", "Ventilateur", 2, BigDecimal.valueOf(25));
            var request = new CreateInterventionRequest(
                    "INT-ITEM-001", clientId, "NG-STARs", null,
                    null, null, "Serveur", "Dell", "R740",
                    "SN456", "Datacenter", "Surchauffe", null, null,
                    null, null, null, null, null,
                    null, null, List.of(itemReq));

            when(interventionRepository.existsByReference("INT-ITEM-001")).thenReturn(false);
            when(interventionRepository.save(any(Intervention.class))).thenAnswer(inv -> {
                var i = inv.getArgument(0, Intervention.class);
                if (i.getId() == null) i.setId(UUID.randomUUID());
                return i;
            });

            var response = interventionService.createIntervention(request, userId);

            assertNotNull(response);
            verify(interventionRepository).save(any(Intervention.class));
        }
    }

    @Nested
    @DisplayName("getIntervention()")
    class GetIntervention {

        @Test
        @DisplayName("Retourne l'intervention quand elle existe")
        void getIntervention_found() {
            when(interventionRepository.findById(interventionId)).thenReturn(Optional.of(intervention));

            var response = interventionService.getIntervention(interventionId, userId, true);

            assertNotNull(response);
            assertEquals("INT-TEST-001", response.reference());
        }

        @Test
        @DisplayName("Lance NotFoundException si introuvable")
        void getIntervention_notFound_throws() {
            when(interventionRepository.findById(interventionId)).thenReturn(Optional.empty());

            assertThrows(NotFoundException.class,
                    () -> interventionService.getIntervention(interventionId, userId, true));
        }
    }

    @Nested
    @DisplayName("deleteIntervention()")
    class DeleteIntervention {

        @Test
        @DisplayName("Désactive l'intervention (soft delete)")
        void deleteIntervention_success() {
            when(interventionRepository.findById(interventionId)).thenReturn(Optional.of(intervention));
            when(interventionRepository.save(any(Intervention.class))).thenAnswer(inv -> inv.getArgument(0));

            interventionService.deleteIntervention(interventionId, userId, true);

            assertFalse(intervention.getActive());
            verify(sseManager).sendEvent(eq("INTERVENTION_DELETED"), any());
        }
    }

    @Nested
    @DisplayName("updateInterventionGps()")
    class UpdateGps {

        @Test
        @DisplayName("Met à jour les coordonnées GPS")
        void updateGps_success() {
            var request = new UpdateInterventionRequest(6.1319, 1.2228);

            when(interventionRepository.findById(interventionId)).thenReturn(Optional.of(intervention));
            when(interventionRepository.save(any(Intervention.class))).thenAnswer(inv -> inv.getArgument(0));

            var response = interventionService.updateInterventionGps(interventionId, request, userId, true);

            assertEquals(6.1319, response.gpsLatitude());
            assertEquals(1.2228, response.gpsLongitude());
        }
    }

    @Nested
    @DisplayName("updateEquipment()")
    class UpdateEquipment {

        @Test
        @DisplayName("Met à jour l'équipement")
        void updateEquipment_success() {
            var request = new UpdateEquipmentRequest(
                    "Dell", "R740", "SN789", "Datacenter", "Bruit anormal");

            when(interventionRepository.findById(interventionId)).thenReturn(Optional.of(intervention));
            when(interventionRepository.save(any(Intervention.class))).thenAnswer(inv -> inv.getArgument(0));

            var response = interventionService.updateEquipment(interventionId, request, userId, true);

            assertEquals("Dell", response.equipmentBrand());
            assertEquals("R740", response.equipmentModel());
        }
    }

    @Nested
    @DisplayName("updateDiagnosis()")
    class UpdateDiagnosis {

        @Test
        @DisplayName("Met à jour le diagnostic")
        void updateDiagnosis_success() {
            var request = new UpdateDiagnosisRequest("Disque dur défectueux", "Remplacement effectué");

            when(interventionRepository.findById(interventionId)).thenReturn(Optional.of(intervention));
            when(interventionRepository.save(any(Intervention.class))).thenAnswer(inv -> inv.getArgument(0));

            var response = interventionService.updateDiagnosis(interventionId, request, userId, true);

            assertEquals("Disque dur défectueux", response.diagnosis());
            assertEquals("Remplacement effectué", response.workDone());
        }
    }

    @Nested
    @DisplayName("updateResult()")
    class UpdateResult {

        @Test
        @DisplayName("Marque comme RÉSOLU")
        void updateResult_resolved() {
            var request = new UpdateResultRequest("RESOLVED");

            when(interventionRepository.findById(interventionId)).thenReturn(Optional.of(intervention));
            when(interventionRepository.save(any(Intervention.class))).thenAnswer(inv -> inv.getArgument(0));

            var response = interventionService.updateResult(interventionId, request, userId, true);

            assertEquals("RESOLVED", response.result());
            assertFalse(response.followUpRecommended());
        }

        @Test
        @DisplayName("Marque comme NON RÉSOLU → follow-up recommandé")
        void updateResult_unresolved_setsFollowUp() {
            var request = new UpdateResultRequest("UNRESOLVED");

            when(interventionRepository.findById(interventionId)).thenReturn(Optional.of(intervention));
            when(interventionRepository.save(any(Intervention.class))).thenAnswer(inv -> inv.getArgument(0));

            var response = interventionService.updateResult(interventionId, request, userId, true);

            assertEquals("UNRESOLVED", response.result());
            assertTrue(response.followUpRecommended());
        }
    }

    @Nested
    @DisplayName("updateRecommendations()")
    class UpdateRecommendations {

        @Test
        @DisplayName("Met à jour les recommandations")
        void updateRecommendations_success() {
            var request = new UpdateRecommendationsRequest("Prévoir maintenance trimestrielle");

            when(interventionRepository.findById(interventionId)).thenReturn(Optional.of(intervention));
            when(interventionRepository.save(any(Intervention.class))).thenAnswer(inv -> inv.getArgument(0));

            var response = interventionService.updateRecommendations(interventionId, request, userId, true);

            assertEquals("Prévoir maintenance trimestrielle", response.recommendations());
        }
    }

    @Nested
    @DisplayName("addItem()")
    class AddItem {

        @Test
        @DisplayName("Ajoute un item à l'intervention")
        void addItem_success() {
            var request = new tg.ngstars.interv.dto.ItemRequest("PART", "Ventilateur", 1, BigDecimal.valueOf(25));

            when(interventionRepository.findById(interventionId)).thenReturn(Optional.of(intervention));
            when(interventionRepository.save(any(Intervention.class))).thenAnswer(inv -> inv.getArgument(0));

            var response = interventionService.addItem(interventionId, request, userId, true);

            assertEquals(1, response.items().size());
            assertEquals("Ventilateur", response.items().getFirst().description());
            assertEquals(BigDecimal.valueOf(25), response.totalCost());
        }
    }

    @Nested
    @DisplayName("getStats()")
    class GetStats {

        @Test
        @DisplayName("Retourne les statistiques correctes")
        void getStats_success() {
            when(interventionRepository.countAll()).thenReturn(100L);
            when(interventionRepository.countActive()).thenReturn(80L);
            when(interventionRepository.countByStatus()).thenReturn(List.of(
                    new Object[]{"PENDING", 10L},
                    new Object[]{"COMPLETED", 50L}
            ));
            when(interventionRepository.countAssigned()).thenReturn(30L);
            when(interventionRepository.countCompleted()).thenReturn(50L);
            when(interventionRepository.countPending()).thenReturn(10L);
            when(interventionRepository.countCancelled()).thenReturn(5L);
            when(interventionRepository.averageDurationMinutes()).thenReturn(45.5);
            when(interventionRepository.sumEstimatedCost()).thenReturn(BigDecimal.valueOf(50000));

            var stats = interventionService.getStats();

            assertEquals(100L, stats.totalInterventions());
            assertEquals(80L, stats.activeInterventions());
            assertEquals(2, stats.countByStatus().size());
            assertEquals(30L, stats.totalAssigned());
            assertEquals(50L, stats.totalCompleted());
            assertEquals(BigDecimal.valueOf(50000), stats.estimatedRevenue());
        }
    }

    @Nested
    @DisplayName("getInterventions()")
    class GetInterventions {

        @Test
        @DisplayName("Retourne les interventions paginées")
        void getInterventions_paged() {
            var page = new PageImpl<>(List.of(intervention));
            when(interventionRepository.findByActiveTrueOrderByCreatedAtDesc(any())).thenReturn(page);

            var result = interventionService.getInterventions(null, null, PageRequest.of(0, 20));

            assertEquals(1, result.getContent().size());
        }
    }

    @Nested
    @DisplayName("syncFromMobile()")
    class SyncFromMobile {

        @Test
        @DisplayName("Crée une nouvelle intervention depuis mobile")
        void syncFromMobile_new() {
            var request = new tg.ngstars.interv.dto.SyncRequest(
                    "INT-SYNC-001", clientId, null, null,
                    null, null, null, null, null,
                    null, null, "PENDING", null,
                    null, null, "local-001", null);

            when(interventionRepository.findByLocalId("local-001")).thenReturn(Optional.empty());
            when(interventionRepository.save(any(Intervention.class))).thenAnswer(inv -> {
                var i = inv.getArgument(0, Intervention.class);
                if (i.getId() == null) i.setId(UUID.randomUUID());
                return i;
            });

            var response = interventionService.syncFromMobile(request, userId, true);

            assertNotNull(response);
            verify(interventionRepository).save(any(Intervention.class));
        }
    }

    @Nested
    @DisplayName("updateSchedule()")
    class UpdateSchedule {

        @Test
        @DisplayName("Calcule la durée en minutes quand endTime est fourni")
        void updateSchedule_calculatesDuration() {
            var start = OffsetDateTime.now();
            var end = start.plusMinutes(90);
            var request = new UpdateScheduleRequest(
                    start.minusMinutes(30), start, start, end);

            when(interventionRepository.findById(interventionId)).thenReturn(Optional.of(intervention));
            when(interventionRepository.save(any(Intervention.class))).thenAnswer(inv -> inv.getArgument(0));

            var response = interventionService.updateSchedule(interventionId, request, userId, true);

            assertEquals(90, response.durationMinutes());
        }
    }
}
