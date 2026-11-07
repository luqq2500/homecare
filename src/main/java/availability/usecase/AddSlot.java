package availability.usecase;

import availability.domain.Availability;
import availability.domain.BookableSlot;
import availability.usecase.dto.AddSlotRequest;
import availability.usecase.dto.AddSlotResponse;
import availability.usecase.port.in.SlotAdder;
import availability.usecase.port.out.AvailabilityRepository;
import availability.usecase.port.out.AvailabilityIEPublisher;
import ie.AvailabilityUpdatedIE;

import java.time.Instant;

public class AddSlot implements SlotAdder {

    private final AvailabilityRepository repository;
    private final AvailabilityIEPublisher publisher;

    public AddSlot(AvailabilityRepository repository, AvailabilityIEPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
    }

    @Override
    public AddSlotResponse add(AddSlotRequest request) {
        Availability availability = repository.findByProviderId(request.providerId())
                .orElseGet(() -> Availability.forProvider(request.providerId()));

        availability.addSlot(BookableSlot.of(request.start(), request.durationHours()));
        repository.save(availability);
        publisher.publish(new AvailabilityUpdatedIE(request.providerId(), Instant.now()));

        return new AddSlotResponse(request.providerId(), availability.slots().size());
    }
}
