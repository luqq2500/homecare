package booking.application;

import booking.application.dto.UserAcceptBookingConfirmationRequest;
import booking.application.dto.UserAcceptBookingConfirmationResponse;
import booking.application.exception.BookingConfirmationNotFound;
import booking.application.exception.BookingNotFoundException;
import booking.application.exception.UserNotFoundException;
import booking.application.port.BookingConfirmationRepository;
import booking.application.port.BookingRepository;
import booking.application.port.UserBookingConfirmationAcceptor;
import booking.application.port.UserRepository;
import booking.domain.Booking;
import booking.domain.BookingConfirmation;
import booking.domain.User;

import java.util.Optional;

public class UserAcceptBookingConfirmation implements UserBookingConfirmationAcceptor {
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final BookingConfirmationRepository bookingConfirmationRepository;

    public UserAcceptBookingConfirmation(UserRepository userRepository, BookingRepository bookingRepository, BookingConfirmationRepository bookingConfirmationRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
        this.bookingConfirmationRepository = bookingConfirmationRepository;
    }

    @Override
    public UserAcceptBookingConfirmationResponse accept(UserAcceptBookingConfirmationRequest request) {
        Optional<User> findUser = userRepository.findById(request.userId());
        if (findUser.isEmpty()){
            throw new UserNotFoundException("User cannot be found.");
        }
        Optional<BookingConfirmation> findBookingConfirmation = bookingConfirmationRepository.findById(request.bookingConfirmationId());
        if (findBookingConfirmation.isEmpty()){
            throw new BookingConfirmationNotFound("Booking confirmation cannot be found.");
        }
        Optional<Booking> findBooking = bookingRepository.findById(request.bookingId());
        if (findBooking.isEmpty()){
            throw new BookingNotFoundException("Booking cannot be found.");
        }

        User user = findUser.get();
        user.validateIsActive();
        BookingConfirmation bookingConfirmation = findBookingConfirmation.get();
        Booking booking = findBooking.get();
        booking.validateBookingRequireConfirmation();

        booking.accept(user, bookingConfirmation);
        bookingRepository.save(booking);

        return new UserAcceptBookingConfirmationResponse(booking);
    }
}
