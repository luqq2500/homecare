package availability.usecase;

import org.junit.jupiter.api.Test;
import availability.domain.Availability;
import availability.domain.BookableSlot;
import availability.usecase.dto.CheckAvailabilityRequest;
import availability.usecase.dto.CheckAvailabilityResponse;
import availability.usecase.port.out.AvailabilityRepository;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class CheckAvailabilityTest {

    private final AvailabilityRepository repository = mock(AvailabilityRepository.class);
    private final CheckAvailability usecase = new CheckAvailability(repository);

    @Test
    void checkReturnsTrueWhenSlotCoversWindow() {
        Instant start = Instant.parse("2026-11-08T14:00:00Z");
        Availability availability = Availability.forProvider("prov-1");
        availability.addSlot(BookableSlot.of(start, 4));
        when(repository.findByProviderId("prov-1")).thenReturn(Optional.of(availability));

        CheckAvailabilityResponse response = usecase.check(
                new CheckAvailabilityRequest("prov-1", start, 2));

        assertEquals("prov-1", response.providerId());
        assertTrue(response.available());
    }

    @Test
    void checkReturnsFalseWhenNoAvailability() {
        Instant start = Instant.parse("2026-11-08T14:00:00Z");
        when(repository.findByProviderId("prov-1")).thenReturn(Optional.empty());

        CheckAvailabilityResponse response = usecase.check(
                new CheckAvailabilityRequest("prov-1", start, 2));

        assertFalse(response.available());
    }
}
