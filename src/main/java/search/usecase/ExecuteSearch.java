package search.usecase;

import search.domain.MatchResult;
import search.domain.SearchCriteria;
import search.usecase.dto.SearchRequest;
import search.usecase.dto.SearchResponse;
import search.usecase.port.in.SearchExecutor;
import search.usecase.port.out.ProviderAvailabilityQuery;

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
