package discovery.usecase;

import discovery.domain.MatchResult;
import discovery.domain.ProviderCandidate;
import discovery.domain.SearchCriteria;
import discovery.usecase.dto.SearchRequest;
import discovery.usecase.dto.SearchResponse;
import discovery.usecase.port.in.SearchExecutor;
import discovery.usecase.port.out.ProviderAvailabilityQuery;

import java.util.ArrayList;
import java.util.List;

public class ExecuteSearch implements SearchExecutor {

    private final ProviderAvailabilityQuery query;

    public ExecuteSearch(ProviderAvailabilityQuery query) {
        this.query = query;
    }

    @Override
    public SearchResponse search(SearchRequest request) {
        SearchCriteria criteria = SearchCriteria.of(
                request.jobs(), request.start(), request.durationHours(), request.helperCount());

        List<MatchResult> matches = new ArrayList<>();
        for (String providerId : query.findCandidates(criteria)) {
            ProviderCandidate candidate = ProviderCandidate.of(
                    providerId,
                    query.isLive(providerId),
                    query.isExactMatch(providerId, criteria) || query.isCloseMatch(providerId, criteria),
                    true,
                    query.isExactMatch(providerId, criteria)
            );
            if (!candidate.isLive()) continue;
            if (candidate.isExactMatch()) {
                matches.add(MatchResult.exact(providerId));
            } else if (candidate.isCloseMatch()) {
                matches.add(MatchResult.close(providerId, query.closeMatchReason(providerId, criteria)));
            }
        }
        return new SearchResponse(List.copyOf(matches));
    }
}
