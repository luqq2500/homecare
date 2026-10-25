package booking.application.port;

import booking.application.dto.ProviderCreateBookingConfirmationRequest;
import booking.application.dto.ProviderCreateBookingConfirmationResponse;

public interface ProviderBookingConfirmationCreator {
    public ProviderCreateBookingConfirmationResponse create(ProviderCreateBookingConfirmationRequest request);
}
