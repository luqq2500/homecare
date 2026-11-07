package availability.usecase.port.in;

import availability.usecase.dto.AddSlotRequest;
import availability.usecase.dto.AddSlotResponse;

public interface SlotAdder {
    AddSlotResponse add(AddSlotRequest request);
}
