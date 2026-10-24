package booking.domain;

import java.util.UUID;

public class BookingId {
    private final String id;
    public BookingId(){
        this.id = UUID.randomUUID().toString();
    }
    public String getId() {
        return id;
    }
}
