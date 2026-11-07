package availability.usecase;

import availability.domain.Availability;
import availability.usecase.dto.CheckAvailabilityRequest;
import availability.usecase.dto.CheckAvailabilityResponse;
import availability.usecase.port.in.AvailabilityChecker;
import availability.usecase.port.out.AvailabilityRepository;

public class CheckAvailability implements AvailabilityChecker {

    private final AvailabilityRepository repository;

    public CheckAvailability(AvailabilityRepository repository) {
        this.repository = repository;
    }

    @Override
    public CheckAvailabilityResponse check(CheckAvailabilityRequest request) {
        Availability availability = repository.findByProviderId(request.providerId())
                .orElseGet(() -> Availability.forProvider(request.providerId()));

        boolean available = availability.isAvailable(request.start(), request.durationHours());
        return new CheckAvailabilityResponse(request.providerId(), available);
    }
}
