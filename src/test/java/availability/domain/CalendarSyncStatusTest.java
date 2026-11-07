package availability.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CalendarSyncStatusTest {

    @Test
    void enumValuesMatchExpected() {
        assertEquals(CalendarSyncStatus.NEVER, CalendarSyncStatus.valueOf("NEVER"));
        assertEquals(CalendarSyncStatus.SYNCED, CalendarSyncStatus.valueOf("SYNCED"));
        assertEquals(CalendarSyncStatus.FAILED, CalendarSyncStatus.valueOf("FAILED"));
    }
}
