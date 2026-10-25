package booking.application;


import booking.application.dto.UserCreateBookingRequestRequest;
import booking.application.dto.UserCreateBookingRequestResponse;
import booking.application.exception.ProviderNotFoundException;
import booking.application.exception.UserNotFoundException;
import booking.application.port.BookingRepository;
import booking.application.port.UserBookingRequestCreator;
import booking.application.port.ProviderRepository;
import booking.application.port.UserRepository;
import booking.domain.Booking;
import booking.domain.Provider;
import booking.domain.User;

import java.util.Optional;

public class UserCreateBookingRequest implements UserBookingRequestCreator {
    private final UserRepository userRepository;
    private final ProviderRepository providerRepository;
    private final BookingRepository bookingRepository;

    public UserCreateBookingRequest(UserRepository userRepository, ProviderRepository providerRepository, BookingRepository bookingRepository) {
        this.userRepository = userRepository;
        this.providerRepository = providerRepository;
        this.bookingRepository = bookingRepository;
    }

    @Override
    public UserCreateBookingRequestResponse request(UserCreateBookingRequestRequest request) {
        Optional<User> findUser = userRepository.findById(request.userId());
        if (findUser.isEmpty()){
            throw new UserNotFoundException("User cannot be found.");
        }
        Optional<Provider> findProvider = providerRepository.findById(request.providerId());
        if (findProvider.isEmpty()){
             throw new ProviderNotFoundException("Provider cannot be found.");
        }

        User user = findUser.get();
        user.validateIsActive();
        Provider provider = findProvider.get();
        provider.validateIsActive();

        Booking booking = new Booking(request.userId(), request.providerId(), request.categoryId(), request.offeringIds(), request.start(), request.end());

        bookingRepository.save(booking);

        return new UserCreateBookingRequestResponse(booking);
    }
}
