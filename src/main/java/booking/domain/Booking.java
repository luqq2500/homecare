package booking.domain;

import java.util.List;
import java.util.Objects;

public class Booking {
    private final String id;
    private final String userId;
    private final String providerId;
    private final String categoryId;
    private List<String> offeringIds;
    private Schedule schedule;
    private BookingPrice bookingPrice;
    private BookingStatus status;

    public Booking(String userId, String providerId, String categoryId, List<String> offeringIds, Schedule schedule){
        this.id = new BookingId().getId();
        this.userId = userId;
        this.providerId = providerId;
        this.categoryId = categoryId;
        this.offeringIds = offeringIds;
        this.schedule = schedule;
        this.bookingPrice = new BookingPrice(0);
        this.status = BookingStatus.REQUESTED;
    }
    public void setNewOfferings(List<String> newOfferingIds){
        this.offeringIds = newOfferingIds;
    }
    public void setNewSchedule(Schedule schedule){
        schedule.validate();
        this.schedule = schedule;
    }
    public void setNewStatus(BookingStatus newStatus){
        this.status.validateStatusLifecycleTransition(newStatus);
        this.status = newStatus;
    }
    public void setNewPrice(BookingPrice newBookingPrice){
        this.bookingPrice = newBookingPrice;
    }
}
