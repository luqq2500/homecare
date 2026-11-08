package provider.usecase.availability.port.out;

import provider.domain.availability.Availability;

import java.util.Optional;

public interface AvailabilityRepository {
    Optional<Availability> findByProviderId(String providerId);
    void save(Availability availability);
}
