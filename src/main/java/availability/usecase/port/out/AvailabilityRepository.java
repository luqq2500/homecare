package availability.usecase.port.out;

import availability.domain.Availability;

import java.util.Optional;

public interface AvailabilityRepository {
    void save(Availability availability);
    Optional<Availability> findByProviderId(String providerId);
}
