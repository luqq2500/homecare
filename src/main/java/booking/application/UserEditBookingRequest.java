package booking.application;

import booking.application.dto.UserEditBookingRequestRequest;
import booking.application.dto.UserEditBookingRequestResponse;
import booking.application.exception.BookingNotFoundException;
import booking.application.exception.UserNotFoundException;
import booking.application.port.BookingRepository;
import booking.application.port.UserBookingRequestEditor;
import booking.application.port.UserRepository;
import booking.domain.Booking;
import booking.domain.User;

import java.util.Optional;

public class UserEditBookingRequest implements UserBookingRequestEditor {
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;

    public UserEditBookingRequest(UserRepository userRepository, BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public UserEditBookingRequestResponse edit(UserEditBookingRequestRequest request) {
        Optional<User> findUser = userRepository.findById(request.userId());
        if (findUser.isEmpty()){
            throw new UserNotFoundException("User cannot be found.");
        }
        Optional<Booking> findBooking = bookingRepository.findById(request.bookingId());
        if (findBooking.isEmpty()){
            throw new BookingNotFoundException("Booking cannot be found.");
        }

        User user = findUser.get();
        user.validateIsActive();
        Booking booking = findBooking.get();
        booking.edit(user.getId(), request.newOfferingIds(), request.newStart(), request.newEnd());
        bookingRepository.save(booking);

        return new UserEditBookingRequestResponse(booking);
    }
}
