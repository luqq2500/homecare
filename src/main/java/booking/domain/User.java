package booking.domain;

import java.util.List;

public class User {
    private String id;
    private UserStatus status;

    public User(String id, UserStatus status){
        this.id=id;
        this.status=status;
    }

    public Booking requestBooking (
            String providerId,
            String categoryId,
            List<String> offeringIds,
            Schedule schedule
    ) throws RuntimeException {
        if (this.status != UserStatus.ACTIVE){
            throw new InvalidCreateBookingRequest("User is not eligible to create a booking request.");
        }
        return new Booking(this.id, providerId, categoryId, offeringIds, schedule);
    }

    public Booking cancelBooking (Booking booking) throws RuntimeException {
        if (this.status != UserStatus.ACTIVE){
            throw new InvalidCancelBooking("User is not eligible to cancel a booking.");
        }
        booking.setNewStatus(BookingStatus.CANCELLED);
        return booking;
    }

    public Booking acceptBookingQuotation(Booking booking, Quotation quotation) throws RuntimeException{
        if (this.status != UserStatus.ACTIVE){
            throw new InvalidAcceptQuotation("User is not eligible to accept booking quotation");
        }
        quotation.validate();
        booking.setNewOfferings(quotation.offeringIds());
        booking.setNewSchedule(quotation.schedule());
        booking.setNewPrice(quotation.price());
        booking.setNewStatus(BookingStatus.BOOKED);
        return booking;
    }
}
