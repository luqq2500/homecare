package provider.usecase.availability.port.in;

import provider.usecase.availability.dto.AddSlotRequest;
import provider.usecase.availability.dto.AddSlotResponse;

public interface SlotAdder {
    AddSlotResponse add(AddSlotRequest request);
}
