package tg.ngstars.interv.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import tg.ngstars.interv.dto.*;
import tg.ngstars.common.exception.ForbiddenException;
import tg.ngstars.common.exception.NotFoundException;
import tg.ngstars.interv.model.Intervention;
import tg.ngstars.interv.model.InterventionItem;
import tg.ngstars.interv.repository.InterventionRepository;

@ExtendWith(MockitoExtension.class)
class InterventionServiceTest {

    @Mock InterventionRepository repo;
    @Mock InterventionStatusService statusService;
    InterventionService service;

    UUID userId = UUID.randomUUID();
    UUID interventionId = UUID.randomUUID();

    Intervention intervention;

    @BeforeEach
    void setUp() {
        service = new InterventionService(repo, statusService);
        intervention = Intervention.builder()
                .id(interventionId)
                .reference("INT-001")
                .clientId(UUID.randomUUID())
                .assignedTo(userId)
                .build();
    }

    @Test
    void updateSchedule_shouldSetTimesAndComputeDuration() {
        when(repo.findById(interventionId)).thenReturn(Optional.of(intervention));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var start = OffsetDateTime.parse("2025-01-15T08:00:00Z");
        var end = OffsetDateTime.parse("2025-01-15T10:30:00Z");
        var req = new UpdateScheduleRequest(null, null, start, end);

        var result = service.updateSchedule(interventionId, req, userId, false);

        assertEquals(start, result.startTime());
        assertEquals(end, result.endTime());
        assertEquals(150, result.durationMinutes());
    }

    @Test
    void updateSchedule_whenNotOwner_throwsForbidden() {
        when(repo.findById(interventionId)).thenReturn(Optional.of(intervention));

        var otherUser = UUID.randomUUID();
        var req = new UpdateScheduleRequest(null, null, null, null);

        assertThrows(ForbiddenException.class,
                () -> service.updateSchedule(interventionId, req, otherUser, false));
    }

    @Test
    void updateSchedule_adminCanBypassOwnership() {
        when(repo.findById(interventionId)).thenReturn(Optional.of(intervention));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var otherUser = UUID.randomUUID();
        var req = new UpdateScheduleRequest(null, null, null, null);

        assertDoesNotThrow(() -> service.updateSchedule(interventionId, req, otherUser, true));
    }

    @Test
    void getIntervention_notFound_throwsNotFound() {
        when(repo.findById(interventionId)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getIntervention(interventionId, userId, false));
    }

    @Test
    void closeIntervention_withAllSignatures_setsCompleted() {
        intervention.setClientSignature("sig-client");
        intervention.setTechnicianSignature("sig-tech");
        intervention.setManagerSignature("sig-mgr");
        when(repo.findById(interventionId)).thenReturn(Optional.of(intervention));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));
        doAnswer(inv -> {
            Intervention i = inv.getArgument(0);
            i.setStatus("COMPLETED");
            i.setSignedAt(OffsetDateTime.now());
            return null;
        }).when(statusService).closeIntervention(eq(intervention), eq(userId), eq(false));

        var result = service.closeIntervention(interventionId, userId, false);

        assertEquals("COMPLETED", result.status());
        assertNotNull(result.signedAt());
    }

    @Test
    void closeIntervention_withoutSignatures_doesNotComplete() {
        when(repo.findById(interventionId)).thenReturn(Optional.of(intervention));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.closeIntervention(interventionId, userId, false);

        assertNotEquals("COMPLETED", result.status());
    }

    @Test
    void syncFromMobile_newIntervention_createsIt() {
        var localId = "local-123";
        var clientId = UUID.randomUUID();
        when(repo.findByLocalId(localId)).thenReturn(Optional.empty());
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var req = new SyncRequest("ref-123", clientId, "Client", null, null, null,
                null, null, null, null, null, "PENDING", OffsetDateTime.now(),
                null, null, localId, null);

        var result = service.syncFromMobile(req, userId, false);

        assertEquals(localId, result.intervention().localId());
        assertEquals(clientId, result.intervention().clientId());
        assertEquals(SyncResponse.SyncAction.CREATED, result.action());
        assertFalse(result.conflict());
    }

    @Test
    void syncFromMobile_existingIntervention_updatesIt() {
        var localId = "local-123";
        intervention.setLocalId(localId);
        when(repo.findByLocalId(localId)).thenReturn(Optional.of(intervention));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var req = new SyncRequest("ref-123", UUID.randomUUID(), null, "e@mail.com", "123-456", "Addr 1",
                null, null, null, null, null, "COMPLETED", null, null, null, localId, null);

        var result = service.syncFromMobile(req, userId, false);

        assertEquals("COMPLETED", result.intervention().status());
        assertEquals("e@mail.com", result.intervention().clientEmail());
        assertEquals("123-456", result.intervention().clientPhone());
        assertEquals("Addr 1", result.intervention().clientAddress());
        assertEquals(SyncResponse.SyncAction.UPDATED, result.action());
        assertFalse(result.conflict());
    }

    @Test
    void syncFromMobile_conflictDetection_returnsConflict() {
        var localId = "local-123";
        intervention.setLocalId(localId);
        intervention.setUpdatedAt(OffsetDateTime.parse("2025-07-15T12:00:00Z"));
        when(repo.findByLocalId(localId)).thenReturn(Optional.of(intervention));

        var clientTime = OffsetDateTime.parse("2025-07-15T10:00:00Z");
        var req = new SyncRequest("ref-123", UUID.randomUUID(), null, null, null, null,
                null, null, null, null, null, "IN_PROGRESS", null, null, null, localId, clientTime);

        var result = service.syncFromMobile(req, userId, false);

        assertTrue(result.conflict());
        assertEquals(SyncResponse.SyncAction.CONFLICT, result.action());
        assertNotNull(result.conflictMessage());
        assertNotNull(result.intervention());
        verify(repo, never()).save(any());
    }

    @Test
    void updateEquipment_shouldSetAllFields() {
        when(repo.findById(interventionId)).thenReturn(Optional.of(intervention));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var req = new UpdateEquipmentRequest("BrandX", "ModelY", "SN123", "Room 1",
                "Won't start", "OP-42", "http://ticket/42");

        var result = service.updateEquipment(interventionId, req, userId, false);

        assertEquals("BrandX", result.equipmentBrand());
        assertEquals("ModelY", result.equipmentModel());
        assertEquals("SN123", result.equipmentSerial());
        assertEquals("Room 1", result.equipmentLocation());
        assertEquals("Won't start", result.reportedIssue());
        assertEquals("OP-42", result.openprojectTicketId());
        assertEquals("http://ticket/42", result.openprojectTicketUrl());
    }

    @Test
    void updateDiagnosis_shouldSetDiagnosisAndWorkDone() {
        when(repo.findById(interventionId)).thenReturn(Optional.of(intervention));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var req = new UpdateDiagnosisRequest("Bad capacitor", "Replaced capacitor");

        var result = service.updateDiagnosis(interventionId, req, userId, false);

        assertEquals("Bad capacitor", result.diagnosis());
        assertEquals("Replaced capacitor", result.workDone());
    }

    @Test
    void updateResult_shouldSetResult() {
        when(repo.findById(interventionId)).thenReturn(Optional.of(intervention));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.updateResult(interventionId, new UpdateResultRequest("COMPLETED"), userId, false);

        assertEquals("COMPLETED", result.result());
    }

    @Test
    void updateInterventionGps_shouldSetCoordinates() {
        when(repo.findById(interventionId)).thenReturn(Optional.of(intervention));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var req = new UpdateInterventionRequest(6.1319, 1.2228);

        var result = service.updateInterventionGps(interventionId, req, userId, false);

        assertEquals(6.1319, result.gpsLatitude());
        assertEquals(1.2228, result.gpsLongitude());
    }

    @Test
    void addItem_shouldAddToIntervention() {
        when(repo.findById(interventionId)).thenReturn(Optional.of(intervention));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var req = new ItemRequest("PART", "New capacitor", 2, new BigDecimal("15.00"));

        var result = service.addItem(interventionId, req, userId, false);

        assertEquals(1, result.items().size());
        assertEquals("New capacitor", result.items().getFirst().description());
    }

    @Test
    void assignIntervention_shouldSetAssignedTo() {
        when(repo.findById(interventionId)).thenReturn(Optional.of(intervention));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var newTech = UUID.randomUUID();
        var result = service.assignIntervention(interventionId, newTech, userId, true);

        assertEquals(newTech, result.assignedTo());
        verify(statusService).assignIntervention(intervention, userId);
    }

    @Test
    void startIntervention_shouldSetInProgress() {
        when(repo.findById(interventionId)).thenReturn(Optional.of(intervention));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.startIntervention(interventionId, userId, false);

        verify(statusService).startIntervention(intervention, userId);
        assertNotNull(result);
    }

    @Test
    void cancelIntervention_shouldDelegateToStatusService() {
        when(repo.findById(interventionId)).thenReturn(Optional.of(intervention));
        when(repo.save(any())).thenAnswer(i -> i.getArgument(0));

        var result = service.cancelIntervention(interventionId, userId, true);

        verify(statusService).cancelIntervention(intervention, userId, true);
        assertNotNull(result);
    }

    @Test
    void cancelIntervention_notAdmin_throwsForbidden() {
        when(repo.findById(interventionId)).thenReturn(Optional.of(intervention));
        doThrow(new ForbiddenException("Only admin or manager can cancel an intervention"))
                .when(statusService).cancelIntervention(intervention, userId, false);

        assertThrows(ForbiddenException.class,
                () -> service.cancelIntervention(interventionId, userId, false));
    }
}
