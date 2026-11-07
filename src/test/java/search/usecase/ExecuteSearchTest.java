package search.usecase;

import org.junit.jupiter.api.Test;
import search.domain.MatchResult;
import search.domain.SearchCriteria;
import search.usecase.dto.SearchRequest;
import search.usecase.dto.SearchResponse;
import search.usecase.port.out.ProviderAvailabilityQuery;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ExecuteSearchTest {

    private final ProviderAvailabilityQuery query = mock(ProviderAvailabilityQuery.class);
    private final ExecuteSearch usecase = new ExecuteSearch(query);

    private static final Instant START = Instant.parse("2026-11-08T14:00:00Z");

    @Test
    void searchReturnsExactAndCloseMatches() {
        when(query.findCandidates(any())).thenReturn(List.of("prov-1", "prov-2", "prov-3"));
        when(query.isExactMatch(eq("prov-1"), any())).thenReturn(true);
        when(query.isExactMatch(eq("prov-2"), any())).thenReturn(false);
        when(query.isCloseMatch(eq("prov-2"), any())).thenReturn(true);
        when(query.closeMatchReason(eq("prov-2"), any())).thenReturn("different helper count");
        when(query.isExactMatch(eq("prov-3"), any())).thenReturn(false);
        when(query.isCloseMatch(eq("prov-3"), any())).thenReturn(false);

        SearchResponse response = usecase.search(
                new SearchRequest(List.of("Kitchen"), START, 2, 1));

        assertEquals(2, response.matches().size());
        MatchResult first = response.matches().get(0);
        assertEquals("prov-1", first.providerId());
        assertTrue(first.isExact());

        MatchResult second = response.matches().get(1);
        assertEquals("prov-2", second.providerId());
        assertFalse(second.isExact());
        assertEquals("different helper count", second.reason());
    }

    @Test
    void searchReturnsEmptyWhenNoCandidates() {
        when(query.findCandidates(any())).thenReturn(List.of());

        SearchResponse response = usecase.search(
                new SearchRequest(List.of("Kitchen"), START, 2, 1));

        assertTrue(response.matches().isEmpty());
    }
}
