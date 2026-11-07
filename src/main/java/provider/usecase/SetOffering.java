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
import provider.usecase.port.out.ProviderIEPublisher;
import ie.ProviderOfferingUpdatedIE;

import java.time.Instant;

public class SetOffering implements OfferingSetter {

    private final ProviderRepository repository;
    private final ProviderIEPublisher publisher;

    public SetOffering(ProviderRepository repository, ProviderIEPublisher publisher) {
        this.repository = repository;
        this.publisher = publisher;
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
        publisher.publish(new ProviderOfferingUpdatedIE(id.value(), Instant.now()));

        return new SetOfferingResponse(id.value(), provider.canReceiveNewReservations());
    }
}
