package tg.ngstars.interv.model;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class InterventionStatusTest {

    @Test
    void fromString_validValues() {
        assertEquals(InterventionStatus.PENDING, InterventionStatus.fromString("PENDING"));
        assertEquals(InterventionStatus.ASSIGNED, InterventionStatus.fromString("ASSIGNED"));
        assertEquals(InterventionStatus.IN_PROGRESS, InterventionStatus.fromString("IN_PROGRESS"));
        assertEquals(InterventionStatus.COMPLETED, InterventionStatus.fromString("COMPLETED"));
        assertEquals(InterventionStatus.CANCELLED, InterventionStatus.fromString("CANCELLED"));
    }

    @Test
    void fromString_caseInsensitive() {
        assertEquals(InterventionStatus.PENDING, InterventionStatus.fromString("pending"));
        assertEquals(InterventionStatus.ASSIGNED, InterventionStatus.fromString("assigned"));
        assertEquals(InterventionStatus.IN_PROGRESS, InterventionStatus.fromString("in_progress"));
        assertEquals(InterventionStatus.COMPLETED, InterventionStatus.fromString("completed"));
        assertEquals(InterventionStatus.CANCELLED, InterventionStatus.fromString("cancelled"));
    }

    @Test
    void fromString_withWhitespace() {
        assertEquals(InterventionStatus.PENDING, InterventionStatus.fromString("  PENDING  "));
    }

    @Test
    void fromString_invalidValue_shouldThrow() {
        var ex = assertThrows(IllegalArgumentException.class,
                () -> InterventionStatus.fromString("BOGUS"));
        assertTrue(ex.getMessage().contains("BOGUS"));
    }

    @Test
    void fromString_null_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> InterventionStatus.fromString(null));
    }

    @Test
    void fromString_blank_shouldThrow() {
        assertThrows(IllegalArgumentException.class,
                () -> InterventionStatus.fromString(""));
        assertThrows(IllegalArgumentException.class,
                () -> InterventionStatus.fromString("   "));
    }

    @Test
    void canTransitionTo_validTransitions() {
        assertTrue(InterventionStatus.PENDING.canTransitionTo("ASSIGNED"));
        assertTrue(InterventionStatus.PENDING.canTransitionTo("CANCELLED"));

        assertTrue(InterventionStatus.ASSIGNED.canTransitionTo("IN_PROGRESS"));
        assertTrue(InterventionStatus.ASSIGNED.canTransitionTo("CANCELLED"));

        assertTrue(InterventionStatus.IN_PROGRESS.canTransitionTo("COMPLETED"));
        assertTrue(InterventionStatus.IN_PROGRESS.canTransitionTo("CANCELLED"));
    }

    @Test
    void canTransitionTo_invalidTransitions() {
        assertFalse(InterventionStatus.PENDING.canTransitionTo("COMPLETED"));
        assertFalse(InterventionStatus.PENDING.canTransitionTo("IN_PROGRESS"));
        assertFalse(InterventionStatus.ASSIGNED.canTransitionTo("COMPLETED"));
        assertFalse(InterventionStatus.ASSIGNED.canTransitionTo("PENDING"));
        assertFalse(InterventionStatus.IN_PROGRESS.canTransitionTo("PENDING"));
        assertFalse(InterventionStatus.IN_PROGRESS.canTransitionTo("ASSIGNED"));
    }

    @Test
    void terminalStates_emptyTransitionSets() {
        assertFalse(InterventionStatus.COMPLETED.canTransitionTo("PENDING"));
        assertFalse(InterventionStatus.COMPLETED.canTransitionTo("ASSIGNED"));
        assertFalse(InterventionStatus.COMPLETED.canTransitionTo("IN_PROGRESS"));
        assertFalse(InterventionStatus.COMPLETED.canTransitionTo("CANCELLED"));

        assertFalse(InterventionStatus.CANCELLED.canTransitionTo("PENDING"));
        assertFalse(InterventionStatus.CANCELLED.canTransitionTo("ASSIGNED"));
        assertFalse(InterventionStatus.CANCELLED.canTransitionTo("IN_PROGRESS"));
        assertFalse(InterventionStatus.CANCELLED.canTransitionTo("COMPLETED"));
    }
}
