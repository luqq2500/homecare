package search.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchCriteriaGherkinTest {

    @Test
    void criteriaAcceptsValidSearchForTwoHoursTwoHelpers() {
        Instant start = Instant.parse("2026-11-10T10:00:00Z");
        SearchCriteria criteria = SearchCriteria.of(
                List.of("Kitchen Care"), start, 2, 2);
        assertEquals(2, criteria.durationHours());
        assertEquals(2, criteria.helperCount());
        assertEquals(start, criteria.start());
    }

    @Test
    void criteriaRejectsInvalidDurationOrHelperCount() {
        Instant start = Instant.parse("2026-11-10T10:00:00Z");
        assertThrows(IllegalArgumentException.class,
                () -> SearchCriteria.of(List.of("Job"), start, 0, 2));
        assertThrows(IllegalArgumentException.class,
                () -> SearchCriteria.of(List.of("Job"), start, 2, 0));
    }
}
