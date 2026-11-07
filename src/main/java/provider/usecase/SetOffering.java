package provider.usecase;

import provider.domain.CancellationTier;
import provider.domain.Offering;
import provider.domain.Provider;
import provider.domain.ProviderId;
import provider.usecase.dto.SetOfferingRequest;
import provider.usecase.dto.SetOfferingResponse;
import provider.usecase.exception.ProviderNotFoundException;
import provider.usecase.port.in.OfferingSetter;
import provider.usecase.port.out.ProviderRepository;

public class SetOffering implements OfferingSetter {

    private final ProviderRepository repository;

    public SetOffering(ProviderRepository repository) {
        this.repository = repository;
    }

    @Override
    public SetOfferingResponse set(SetOfferingRequest request) {
        ProviderId id = ProviderId.of(request.providerId());
        Provider provider = repository.findById(id)
                .orElseThrow(() -> new ProviderNotFoundException("Provider not found: " + id.value()));

        Offering offering = Offering.of(
                request.jobs(),
                request.tools(),
                request.hourlyRatePerHelper(),
                request.minHours(),
                request.maxHours(),
                request.helperCount(),
                CancellationTier.valueOf(request.cancellationTier()),
                request.travelFeeIncluded()
        );
        provider.setOffering(offering);
        repository.save(provider);

        return new SetOfferingResponse(id.value(), provider.canReceiveNewReservations());
    }
}
