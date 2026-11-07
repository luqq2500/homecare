package reservation.domain;

import java.time.Instant;
import java.util.Objects;

/**
 * Mandatory photo or video evidence attached when the provider marks the job completed.
 * Kept for the evidence retention period (7 days after resolution).
 */
public final class CompletionEvidence {

    public static final int RETENTION_DAYS = 7;

    private final String evidenceReference;
    private final Instant submittedAt;

    private CompletionEvidence(String evidenceReference, Instant submittedAt) {
        this.evidenceReference = Objects.requireNonNull(evidenceReference);
        this.submittedAt = Objects.requireNonNull(submittedAt);
    }

    public static CompletionEvidence of(String evidenceReference, Instant submittedAt) {
        if (evidenceReference == null || evidenceReference.isBlank()) {
            throw new IllegalArgumentException("Evidence is required");
        }
        return new CompletionEvidence(evidenceReference, submittedAt);
    }

    public String evidenceReference() { return evidenceReference; }
    public Instant submittedAt() { return submittedAt; }
}
