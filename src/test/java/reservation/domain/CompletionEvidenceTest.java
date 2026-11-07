package reservation.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;

import static org.junit.jupiter.api.Assertions.*;

class CompletionEvidenceTest {

    @Test
    void evidenceRequiresNonBlankReference() {
        assertThrows(IllegalArgumentException.class,
                () -> CompletionEvidence.of("", Instant.now()));
        assertThrows(IllegalArgumentException.class,
                () -> CompletionEvidence.of("   ", Instant.now()));
        assertThrows(IllegalArgumentException.class,
                () -> CompletionEvidence.of(null, Instant.now()));
    }

    @Test
    void evidenceStoresReferenceAndSubmittedAt() {
        Instant now = Instant.parse("2026-11-08T16:00:00Z");
        CompletionEvidence evidence = CompletionEvidence.of("media-123", now);
        assertEquals("media-123", evidence.evidenceReference());
        assertEquals(now, evidence.submittedAt());
        assertEquals(7, CompletionEvidence.RETENTION_DAYS);
    }
}
