package booking.application;

import booking.application.dto.ProviderCreateBookingConfirmationRequest;
import booking.application.dto.ProviderCreateBookingConfirmationResponse;
import booking.application.exception.BookingNotFoundException;
import booking.application.port.BookingConfirmationRepository;
import booking.application.port.BookingRepository;
import booking.application.port.ProviderBookingConfirmationCreator;
import booking.application.port.ProviderRepository;
import booking.domain.Booking;
import booking.domain.BookingConfirmation;
import booking.domain.BookingConfirmationID;
import booking.domain.Provider;

import java.nio.file.ProviderNotFoundException;
import java.util.Optional;

public class ProviderCreateBookingConfirmation implements ProviderBookingConfirmationCreator {
    private final ProviderRepository providerRepository;
    private final BookingRepository bookingRepository;
    private final BookingConfirmationRepository bookingConfirmationRepository;

    public ProviderCreateBookingConfirmation(ProviderRepository providerRepository, BookingRepository bookingRepository, BookingConfirmationRepository bookingConfirmationRepository) {
        this.providerRepository = providerRepository;
        this.bookingRepository = bookingRepository;
        this.bookingConfirmationRepository = bookingConfirmationRepository;
    }

    @Override
    public ProviderCreateBookingConfirmationResponse create(ProviderCreateBookingConfirmationRequest request) {
        Optional<Provider> findProvider = providerRepository.findById(request.providerId());
        if (findProvider.isEmpty()){
            throw new ProviderNotFoundException("Provider cannot be found.");
        }
        Optional<Booking> findBooking = bookingRepository.findById(request.bookingId());
        if (findBooking.isEmpty()){
            throw new BookingNotFoundException("Booking cannot be found.");
        }

        Provider provider = findProvider.get();
        provider.validateIsActive();
        Booking booking = findBooking.get();
        booking.validateBookingRequireConfirmation();

        BookingConfirmation confirmation = new BookingConfirmation(
                new BookingConfirmationID().getId(),
                request.providerId(),
                request.bookingId(),
                request.start(),
                request.end(),
                request.quotes()
        );
        bookingConfirmationRepository.save(confirmation);

        return new ProviderCreateBookingConfirmationResponse(confirmation);
    }
}
