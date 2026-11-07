package discovery.usecase.port.out;

import discovery.domain.SearchCriteria;

import java.util.List;

public interface ProviderAvailabilityQuery {
    List<String> findCandidates(SearchCriteria criteria);
    boolean isLive(String providerId);
    boolean isExactMatch(String providerId, SearchCriteria criteria);
    boolean isCloseMatch(String providerId, SearchCriteria criteria);
    String closeMatchReason(String providerId, SearchCriteria criteria);
}
