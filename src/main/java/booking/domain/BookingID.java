package booking.domain;

import java.util.UUID;

public class BookingID {
    private final String id;
    public BookingID(){
        this.id = UUID.randomUUID().toString();
    }
    public String getId() {
        return id;
    }
}
