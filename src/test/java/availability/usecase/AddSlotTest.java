package availability.usecase;

import org.junit.jupiter.api.Test;
import availability.domain.Availability;
import availability.usecase.dto.AddSlotRequest;
import availability.usecase.dto.AddSlotResponse;
import availability.usecase.port.out.AvailabilityRepository;

import java.time.Instant;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AddSlotTest {

    private final AvailabilityRepository repository = mock(AvailabilityRepository.class);
    private final AddSlot usecase = new AddSlot(repository);

    @Test
    void addSlotCreatesAvailabilityWhenNoneExists() {
        Instant start = Instant.parse("2026-11-08T14:00:00Z");
        when(repository.findByProviderId("prov-1")).thenReturn(Optional.empty());

        AddSlotResponse response = usecase.add(new AddSlotRequest("prov-1", start, 2));

        assertEquals("prov-1", response.providerId());
        assertEquals(1, response.slotCount());
        verify(repository).save(any(Availability.class));
    }

    @Test
    void addSlotOnExistingAvailabilityIncreasesCount() {
        Instant start = Instant.parse("2026-11-08T14:00:00Z");
        Availability existing = Availability.forProvider("prov-1");
        when(repository.findByProviderId("prov-1")).thenReturn(Optional.of(existing));

        AddSlotResponse response = usecase.add(new AddSlotRequest("prov-1", start, 2));

        assertEquals(1, response.slotCount());
        verify(repository).save(existing);
    }
}
