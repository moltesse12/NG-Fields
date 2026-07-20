package tg.ngstars.interv.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import tg.ngstars.common.exception.ConflictException;
import tg.ngstars.common.exception.ForbiddenException;
import tg.ngstars.interv.model.Intervention;
import tg.ngstars.interv.model.InterventionStatus;

class InterventionStatusServiceTest {

    InterventionStatusService statusService;
    UUID userId = UUID.randomUUID();
    UUID techId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        statusService = new InterventionStatusService();
    }

    @Test
    void validateTransition_validTransition_shouldPass() {
        var intervention = Intervention.builder()
                .reference("INT-001")
                .clientId(UUID.randomUUID())
                .status("PENDING")
                .build();

        assertDoesNotThrow(() -> statusService.validateTransition(intervention, "ASSIGNED"));
    }

    @Test
    void validateTransition_invalidTransition_shouldThrowConflict() {
        var intervention = Intervention.builder()
                .reference("INT-002")
                .clientId(UUID.randomUUID())
                .status("PENDING")
                .build();

        var ex = assertThrows(ConflictException.class,
                () -> statusService.validateTransition(intervention, "IN_PROGRESS"));
        assertTrue(ex.getMessage().contains("PENDING"));
        assertTrue(ex.getMessage().contains("IN_PROGRESS"));
    }

    @Test
    void validateTransition_completedToAnything_shouldThrowConflict() {
        var intervention = Intervention.builder()
                .reference("INT-003")
                .clientId(UUID.randomUUID())
                .status("COMPLETED")
                .build();

        assertThrows(ConflictException.class,
                () -> statusService.validateTransition(intervention, "PENDING"));
        assertThrows(ConflictException.class,
                () -> statusService.validateTransition(intervention, "ASSIGNED"));
        assertThrows(ConflictException.class,
                () -> statusService.validateTransition(intervention, "IN_PROGRESS"));
        assertThrows(ConflictException.class,
                () -> statusService.validateTransition(intervention, "CANCELLED"));
    }

    @Test
    void closeIntervention_withAllSignatures_shouldComplete() {
        var intervention = Intervention.builder()
                .reference("INT-004")
                .clientId(UUID.randomUUID())
                .assignedTo(techId)
                .status("IN_PROGRESS")
                .clientSignature("sig-client")
                .technicianSignature("sig-tech")
                .managerSignature("sig-mgr")
                .build();

        statusService.closeIntervention(intervention, techId, false);

        assertEquals(InterventionStatus.COMPLETED.name(), intervention.getStatus());
        assertNotNull(intervention.getSignedAt());
    }

    @Test
    void closeIntervention_withoutSignatures_shouldThrowConflict() {
        var intervention = Intervention.builder()
                .reference("INT-005")
                .clientId(UUID.randomUUID())
                .assignedTo(techId)
                .status("IN_PROGRESS")
                .build();

        var ex = assertThrows(ConflictException.class,
                () -> statusService.closeIntervention(intervention, techId, false));
        assertTrue(ex.getMessage().contains("signatures"));
    }

    @Test
    void closeIntervention_notAssignedAndNotAdmin_shouldThrowForbidden() {
        var intervention = Intervention.builder()
                .reference("INT-006")
                .clientId(UUID.randomUUID())
                .assignedTo(techId)
                .status("IN_PROGRESS")
                .clientSignature("sig-client")
                .technicianSignature("sig-tech")
                .managerSignature("sig-mgr")
                .build();

        var otherUser = UUID.randomUUID();
        assertThrows(ForbiddenException.class,
                () -> statusService.closeIntervention(intervention, otherUser, false));
    }

    @Test
    void assignIntervention_shouldSetAssigned() {
        var intervention = Intervention.builder()
                .reference("INT-007")
                .clientId(UUID.randomUUID())
                .status("PENDING")
                .build();

        statusService.assignIntervention(intervention, userId);

        assertEquals(InterventionStatus.ASSIGNED.name(), intervention.getStatus());
    }

    @Test
    void startIntervention_shouldSetInProgress() {
        var intervention = Intervention.builder()
                .reference("INT-008")
                .clientId(UUID.randomUUID())
                .status("ASSIGNED")
                .build();

        statusService.startIntervention(intervention, userId);

        assertEquals(InterventionStatus.IN_PROGRESS.name(), intervention.getStatus());
    }

    @Test
    void cancelIntervention_admin_shouldCancel() {
        var intervention = Intervention.builder()
                .reference("INT-009")
                .clientId(UUID.randomUUID())
                .status("PENDING")
                .build();

        statusService.cancelIntervention(intervention, userId, true);

        assertEquals(InterventionStatus.CANCELLED.name(), intervention.getStatus());
    }

    @Test
    void cancelIntervention_nonAdmin_shouldThrowForbidden() {
        var intervention = Intervention.builder()
                .reference("INT-010")
                .clientId(UUID.randomUUID())
                .status("PENDING")
                .build();

        assertThrows(ForbiddenException.class,
                () -> statusService.cancelIntervention(intervention, userId, false));
    }
}
