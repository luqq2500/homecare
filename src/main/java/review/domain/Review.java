package review.domain;

import java.time.Instant;
import java.util.Objects;
import java.util.Set;

/**
 * A review left by a customer for a completed reservation.
 * One review per reservation. Rating 1–5. Tags optional.
 */
public final class Review {

    private final String reservationId;
    private final String providerId;
    private final int rating;
    private final String comment;
    private final Set<ReviewTag> tags;
    private final Instant submittedAt;

    private Review(String reservationId,
                   String providerId,
                   int rating,
                   String comment,
                   Set<ReviewTag> tags,
                   Instant submittedAt) {
        this.reservationId = Objects.requireNonNull(reservationId);
        this.providerId = Objects.requireNonNull(providerId);
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
        this.comment = comment == null ? "" : comment;
        this.tags = Set.copyOf(tags == null ? Set.of() : tags);
        this.submittedAt = Objects.requireNonNull(submittedAt);
    }

    public static Review submit(String reservationId,
                                String providerId,
                                int rating,
                                String comment,
                                Set<ReviewTag> tags,
                                Instant submittedAt) {
        return new Review(reservationId, providerId, rating, comment, tags, submittedAt);
    }

    public String reservationId() { return reservationId; }
    public String providerId() { return providerId; }
    public int rating() { return rating; }
    public String comment() { return comment; }
    public Set<ReviewTag> tags() { return tags; }
    public Instant submittedAt() { return submittedAt; }
}
