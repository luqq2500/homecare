package availability.usecase;

import availability.domain.Availability;
import availability.domain.BookableSlot;
import availability.usecase.dto.AddSlotRequest;
import availability.usecase.dto.AddSlotResponse;
import availability.usecase.port.in.SlotAdder;
import availability.usecase.port.out.AvailabilityRepository;

public class AddSlot implements SlotAdder {

    private final AvailabilityRepository repository;

    public AddSlot(AvailabilityRepository repository) {
        this.repository = repository;
    }

    @Override
    public AddSlotResponse add(AddSlotRequest request) {
        Availability availability = repository.findByProviderId(request.providerId())
                .orElseGet(() -> Availability.forProvider(request.providerId()));

        availability.addSlot(BookableSlot.of(request.start(), request.durationHours()));
        repository.save(availability);

        return new AddSlotResponse(request.providerId(), availability.slots().size());
    }
}
