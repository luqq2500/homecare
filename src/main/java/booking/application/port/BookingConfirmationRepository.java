package booking.application.port;

import booking.domain.BookingConfirmation;

import java.util.Optional;

public interface BookingConfirmationRepository {
    void save(BookingConfirmation confirmation);
    Optional<BookingConfirmation> findById(String bookingConfirmationId);
}
