package provider.usecase.availability;

import provider.domain.availability.Availability;
import provider.domain.availability.BookableSlot;
import provider.usecase.availability.dto.AddSlotRequest;
import provider.usecase.availability.dto.AddSlotResponse;
import provider.usecase.availability.port.in.SlotAdder;
import provider.usecase.availability.port.out.AvailabilityRepository;

import java.time.Instant;

public class AddSlot implements SlotAdder {

    private final AvailabilityRepository repository;

    public AddSlot(AvailabilityRepository repository) {
        this.repository = repository;
    }

    @Override
    public AddSlotResponse add(AddSlotRequest request) {
        Availability availability = repository.findByProviderId(request.providerId())
                .orElseGet(() -> Availability.forProvider(request.providerId()));
        availability.addSlot(BookableSlot.of(Instant.parse(request.start()), request.durationHours()));
        repository.save(availability);
        return new AddSlotResponse(request.providerId(), availability.slots().size());
    }
}
