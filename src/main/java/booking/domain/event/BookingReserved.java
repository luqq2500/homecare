package booking.domain.event;

import booking.domain.BookingConfirmation;

import java.time.LocalDateTime;

public class BookingReserved implements DomainEvent {
    private final String userId;
    private final BookingConfirmation confirmation;
    private final LocalDateTime occurredOn;

    public BookingReserved(String userId, BookingConfirmation bookingConfirmation) {
        this.userId = userId;
        this.confirmation = bookingConfirmation;
        this.occurredOn = LocalDateTime.now();
    }

    @Override
    public LocalDateTime occurredOn() {
        return this.occurredOn;
    }
}
