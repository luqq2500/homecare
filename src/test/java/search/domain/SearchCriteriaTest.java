package search.domain;

import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchCriteriaTest {

    private static final Instant ON_BOUNDARY = Instant.parse("2026-11-08T14:00:00Z");
    private static final Instant OFF_BOUNDARY = Instant.parse("2026-11-08T14:07:00Z");

    @Test
    void ofAcceptsValidCriteria() {
        SearchCriteria criteria = SearchCriteria.of(
                List.of("Kitchen care"), ON_BOUNDARY, 2, 1);
        assertEquals(List.of("Kitchen care"), criteria.jobs());
        assertEquals(ON_BOUNDARY, criteria.start());
        assertEquals(2, criteria.durationHours());
        assertEquals(1, criteria.helperCount());
    }

    @Test
    void ofRejectsDurationOutsideOneToFour() {
        assertThrows(IllegalArgumentException.class,
                () -> SearchCriteria.of(List.of("Job"), ON_BOUNDARY, 0, 1));
        assertThrows(IllegalArgumentException.class,
                () -> SearchCriteria.of(List.of("Job"), ON_BOUNDARY, 5, 1));
    }

    @Test
    void ofRejectsHelperCountOutsideOneToTen() {
        assertThrows(IllegalArgumentException.class,
                () -> SearchCriteria.of(List.of("Job"), ON_BOUNDARY, 1, 0));
        assertThrows(IllegalArgumentException.class,
                () -> SearchCriteria.of(List.of("Job"), ON_BOUNDARY, 1, 11));
    }

    @Test
    void ofRejectsStartNotOn15MinBoundary() {
        assertThrows(IllegalArgumentException.class,
                () -> SearchCriteria.of(List.of("Job"), OFF_BOUNDARY, 1, 1));
    }
}
