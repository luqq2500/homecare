package availability.usecase.port.in;

import availability.usecase.dto.CheckAvailabilityRequest;
import availability.usecase.dto.CheckAvailabilityResponse;

public interface AvailabilityChecker {
    CheckAvailabilityResponse check(CheckAvailabilityRequest request);
}
