package booking.domain;

import java.util.UUID;

public class BookingConfirmationID {
    private final String id;
    public BookingConfirmationID() {
        this.id = UUID.randomUUID().toString();
    }
    public String getId() {
        return id;
    }
}
