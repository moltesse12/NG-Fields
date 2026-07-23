package tg.ngstars.interv.service;

import static org.junit.jupiter.api.Assertions.*;

import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import tg.ngstars.common.exception.ConflictException;
import tg.ngstars.common.exception.ForbiddenException;
import tg.ngstars.interv.model.Intervention;
import tg.ngstars.interv.model.InterventionStatus;

@DisplayName("InterventionStatusService")
class InterventionStatusServiceTest {

    private final InterventionStatusService statusService = new InterventionStatusService();
    private UUID userId;
    private UUID assignedUserId;

    @BeforeEach
    void setUp() {
        userId = UUID.randomUUID();
        assignedUserId = UUID.randomUUID();
    }

    private Intervention buildIntervention(String status) {
        return Intervention.builder()
                .id(UUID.randomUUID())
                .reference("INT-STATUS-001")
                .clientId(UUID.randomUUID())
                .status(status)
                .assignedTo(assignedUserId)
                .active(true)
                .items(new java.util.ArrayList<>())
                .build();
    }

    @Nested
    @DisplayName("validateTransition()")
    class ValidateTransition {

        @Test
        @DisplayName("PENDING → ASSIGNED est valide")
        void pendingToAssigned_valid() {
            var i = buildIntervention("PENDING");
            assertDoesNotThrow(() -> statusService.validateTransition(i, "ASSIGNED"));
        }

        @Test
        @DisplayName("PENDING → IN_PROGRESS est invalide")
        void pendingToInProgress_invalid() {
            var i = buildIntervention("PENDING");
            assertThrows(ConflictException.class,
                    () -> statusService.validateTransition(i, "IN_PROGRESS"));
        }

        @Test
        @DisplayName("COMPLETED → n'importe quoi est invalide")
        void completedToAnything_invalid() {
            var i = buildIntervention("COMPLETED");
            assertThrows(ConflictException.class,
                    () -> statusService.validateTransition(i, "PENDING"));
        }

        @Test
        @DisplayName("ASSIGNED → IN_PROGRESS est valide")
        void assignedToInProgress_valid() {
            var i = buildIntervention("ASSIGNED");
            assertDoesNotThrow(() -> statusService.validateTransition(i, "IN_PROGRESS"));
        }

        @Test
        @DisplayName("IN_PROGRESS → COMPLETED est valide")
        void inProgressToCompleted_valid() {
            var i = buildIntervention("IN_PROGRESS");
            assertDoesNotThrow(() -> statusService.validateTransition(i, "COMPLETED"));
        }

        @Test
        @DisplayName("PENDING → CANCELLED est valide")
        void pendingToCancelled_valid() {
            var i = buildIntervention("PENDING");
            assertDoesNotThrow(() -> statusService.validateTransition(i, "CANCELLED"));
        }

        @Test
        @DisplayName("IN_PROGRESS → CANCELLED est valide")
        void inProgressToCancelled_valid() {
            var i = buildIntervention("IN_PROGRESS");
            assertDoesNotThrow(() -> statusService.validateTransition(i, "CANCELLED"));
        }
    }

    @Nested
    @DisplayName("assignIntervention()")
    class AssignIntervention {

        @Test
        @DisplayName("Passe à ASSIGNED")
        void assign_setsStatus() {
            var i = buildIntervention("PENDING");
            statusService.assignIntervention(i, userId);
            assertEquals("ASSIGNED", i.getStatus());
        }

        @Test
        @DisplayName("Rejette si IN_PROGRESS")
        void assign_fromInProgress_throws() {
            var i = buildIntervention("IN_PROGRESS");
            assertThrows(ConflictException.class,
                    () -> statusService.assignIntervention(i, userId));
        }
    }

    @Nested
    @DisplayName("startIntervention()")
    class StartIntervention {

        @Test
        @DisplayName("Passe à IN_PROGRESS")
        void start_setsStatus() {
            var i = buildIntervention("ASSIGNED");
            statusService.startIntervention(i, userId);
            assertEquals("IN_PROGRESS", i.getStatus());
        }

        @Test
        @DisplayName("Rejette si PENDING (pas assigné)")
        void start_fromPending_throws() {
            var i = buildIntervention("PENDING");
            assertThrows(ConflictException.class,
                    () -> statusService.startIntervention(i, userId));
        }
    }

    @Nested
    @DisplayName("closeIntervention()")
    class CloseIntervention {

        @Test
        @DisplayName("Passe à COMPLETED si signatures présentes")
        void close_withSignatures_setsStatus() {
            var i = buildIntervention("IN_PROGRESS");
            i.setClientSignature("sig-client");
            i.setTechnicianSignature("sig-tech");
            i.setManagerSignature("sig-manager");

            statusService.closeIntervention(i, assignedUserId, false);
            assertEquals("COMPLETED", i.getStatus());
            assertNotNull(i.getSignedAt());
        }

        @Test
        @DisplayName("Rejette si signatures manquantes")
        void close_withoutSignatures_throws() {
            var i = buildIntervention("IN_PROGRESS");
            assertThrows(ConflictException.class,
                    () -> statusService.closeIntervention(i, assignedUserId, false));
        }

        @Test
        @DisplayName("Admin peut clôturer sans être assigné")
        void close_adminCanClose() {
            var i = buildIntervention("IN_PROGRESS");
            i.setClientSignature("sig-client");
            i.setTechnicianSignature("sig-tech");
            i.setManagerSignature("sig-manager");

            statusService.closeIntervention(i, userId, true);
            assertEquals("COMPLETED", i.getStatus());
        }

        @Test
        @DisplayName("Non assigné ne peut pas clôturer")
        void close_notAssigned_throws() {
            var i = buildIntervention("IN_PROGRESS");
            i.setClientSignature("sig-client");
            i.setTechnicianSignature("sig-tech");
            i.setManagerSignature("sig-manager");

            var otherUserId = UUID.randomUUID();
            assertThrows(ForbiddenException.class,
                    () -> statusService.closeIntervention(i, otherUserId, false));
        }
    }

    @Nested
    @DisplayName("cancelIntervention()")
    class CancelIntervention {

        @Test
        @DisplayName("Admin peut annuler")
        void cancel_admin_setsStatus() {
            var i = buildIntervention("PENDING");
            statusService.cancelIntervention(i, userId, true);
            assertEquals("CANCELLED", i.getStatus());
        }

        @Test
        @DisplayName("Non-admin ne peut pas annuler")
        void cancel_nonAdmin_throws() {
            var i = buildIntervention("PENDING");
            assertThrows(ForbiddenException.class,
                    () -> statusService.cancelIntervention(i, userId, false));
        }
    }
}
