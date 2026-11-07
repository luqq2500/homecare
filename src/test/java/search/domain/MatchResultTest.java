package search.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MatchResultTest {

    @Test
    void exactHasNoReason() {
        MatchResult result = MatchResult.exact("prov-1");
        assertEquals("prov-1", result.providerId());
        assertTrue(result.isExact());
        assertNull(result.reason());
    }

    @Test
    void closeRequiresReason() {
        MatchResult result = MatchResult.close("prov-2", "different helper count");
        assertEquals("prov-2", result.providerId());
        assertFalse(result.isExact());
        assertEquals("different helper count", result.reason());
    }

    @Test
    void closeRejectsNullReason() {
        assertThrows(NullPointerException.class,
                () -> MatchResult.close("prov-3", null));
    }
}
