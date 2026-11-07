package discovery.usecase;

import discovery.domain.MatchResult;
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
            if (query.isExactMatch(providerId, criteria)) {
                matches.add(MatchResult.exact(providerId));
            } else if (query.isCloseMatch(providerId, criteria)) {
                matches.add(MatchResult.close(providerId, query.closeMatchReason(providerId, criteria)));
            }
        }
        return new SearchResponse(List.copyOf(matches));
    }
}
