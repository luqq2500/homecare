package booking.domain;

import booking.domain.event.BookingCancelled;
import booking.domain.event.BookingRequested;
import booking.domain.event.BookingReserved;
import booking.domain.event.DomainEvent;
import booking.domain.exception.InvalidBookingId;
import booking.domain.exception.InvalidBookingSchedule;
import booking.domain.exception.MismatchBookingProviderId;
import booking.domain.exception.MismatchBookingUserId;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Booking {
    private final String id;
    private final String userId;
    private final String providerId;
    private final String categoryId;
    private List<String> offeringIds;
    private LocalDateTime start;
    private LocalDateTime end;
    private BookingStatus status;
    private int priceTotal;
    private List<DomainEvent> events = new ArrayList<>();

    public Booking(
            String userId,
            String providerId,
            String categoryId,
            List<String> offeringIds,
            LocalDateTime start,
            LocalDateTime end
    ) {
        this.id = new BookingId().getId();
        this.userId = userId;
        this.providerId = providerId;
        this.categoryId = categoryId;
        this.offeringIds = List.copyOf(offeringIds);
        this.validateSchedule(start, end);
        this.start = start;
        this.end = end;
        this.status = BookingStatus.REQUESTED;
        events.add(new BookingRequested(userId, providerId, categoryId, offeringIds, start, end));
    }

    public void cancel(User user) {
        validateIsBelongToUser(user.getId());
        transitionStatusTo(BookingStatus.CANCELLED);
        events.add(new BookingCancelled(user.getId()));
    }

    public void accept(User user, BookingConfirmation bookingConfirmation){
        validateIsBelongToBooking(bookingConfirmation.bookingId());
        validateIsBelongToUser(user.getId());
        validateIsBelongToProvider(bookingConfirmation.providerId());
        this.offeringIds = List.copyOf(bookingConfirmation.offeringIds());
        this.start = bookingConfirmation.start();
        this.end = bookingConfirmation.end();
        this.priceTotal = bookingConfirmation.getPriceTotal();
        transitionStatusTo(BookingStatus.RESERVED);
        events.add(new BookingReserved(userId, bookingConfirmation));
    }

    public List<DomainEvent> getEvents(){
        return this.events;
    }

    private void validateIsBelongToBooking(String bookingId) throws RuntimeException {
        if (!this.id.equals(bookingId)){
            throw new InvalidBookingId("Booking ID isn't belong to the booking.");
        }
    }

    private void validateIsBelongToProvider(String providerId) throws RuntimeException {
        if (!this.providerId.equals(providerId)){
            throw new MismatchBookingProviderId("This booking isn't belong to the provider.");
        }
    }

    private void validateIsBelongToUser(String userId) throws RuntimeException {
        if (!this.userId.equals(userId)){
            throw new MismatchBookingUserId("This booking isn't belong to the user.");
        }
    }

    private void transitionStatusTo(BookingStatus bookingStatus) {
        if (this.status != null){
            this.status.validateStatusLifecycleTransition(bookingStatus);
        }
        this.status = bookingStatus;
    }

    private void validateSchedule(LocalDateTime start, LocalDateTime end) throws RuntimeException {
        if (start.isAfter(end)){
            throw new InvalidBookingSchedule("Booking schedule is invalid.");
        }
    }
}
