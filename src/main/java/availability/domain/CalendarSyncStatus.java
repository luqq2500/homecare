package availability.domain;

/**
 * Outcome of a calendar sync. Actual sync is external.
 */
public enum CalendarSyncStatus {
    NEVER,
    SYNCED,
    FAILED
}
