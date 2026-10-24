package booking.domain;

import java.util.List;
import java.util.Map;

public enum BookingStatus {
    REQUESTED,
    BOOKED,
    CANCELLED,
    COMPLETED;

    // Defines the allowed transitions for each state
    private static final Map<BookingStatus, List<BookingStatus>> STATUS_LIFECYCLE = Map.of(
            REQUESTED, List.of(BOOKED, CANCELLED),
            BOOKED, List.of(COMPLETED, CANCELLED),
            CANCELLED, List.of(), // End state
            COMPLETED, List.of()  // End state
    );

    public void validateStatusLifecycleTransition(BookingStatus newStatus) throws RuntimeException {
        // 1. Guard clause for same status transition
        if (this.equals(newStatus)) {
            throw new InvalidBookingStatusLifecycle("Same booking status cannot be transitioned.");
        }
        // 2. Verify if the target status is allowed from the current status
        List<BookingStatus> validNextStates = STATUS_LIFECYCLE.get(this);
        if (validNextStates == null || !validNextStates.contains(newStatus)) {
            throw new InvalidBookingStatusLifecycle(
                    String.format("Invalid transition: Cannot change status from %s to %s.", this, newStatus)
            );
        }
    }
}

