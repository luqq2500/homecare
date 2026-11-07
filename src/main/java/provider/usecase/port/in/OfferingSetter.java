package provider.usecase.port.in;

import provider.usecase.dto.SetOfferingRequest;
import provider.usecase.dto.SetOfferingResponse;

public interface OfferingSetter {
    SetOfferingResponse set(SetOfferingRequest request);
}
