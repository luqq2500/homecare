package search.usecase.port.out;

import search.domain.SearchCriteria;

import java.util.List;

/**
 * Outbound port that reads provider + availability data.
 * Implementations live in infra and may use events or direct queries.
 */
public interface ProviderAvailabilityQuery {
    List<String> findCandidates(SearchCriteria criteria);
    boolean isExactMatch(String providerId, SearchCriteria criteria);
    boolean isCloseMatch(String providerId, SearchCriteria criteria);
    String closeMatchReason(String providerId, SearchCriteria criteria);
}
