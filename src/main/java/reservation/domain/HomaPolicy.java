package reservation.domain;

/**
 * Homa-set policy values. Same for every reservation.
 * Providers only choose the cancellation tier.
 */
public final class HomaPolicy {

    public static final int CHECK_IN_PERIOD_MINUTES = 15;
    public static final int CHECK_IN_RADIUS_METRES = 300;
    public static final int CUSTOMER_NO_SHOW_WAIT_MINUTES = 15;
    public static final int REQUEST_EXPIRY_HOURS = 24;
    public static final int COMPLETION_REMINDER_MINUTES = 30;
    public static final int EVIDENCE_RETENTION_DAYS = 7;
    public static final int DISPUTE_WINDOW_EXTENSION_HOURS = 1;
    public static final int FEE_WINDOW_HOURS = 24;
    public static final int PROVIDER_LATE_CANCELLATION_FINE_PERCENT = 20;
    public static final int PROVIDER_NO_SHOW_FINE_PERCENT = 30;
    public static final int CUSTOMER_NO_SHOW_HOURS_PERCENT = 30;

    private HomaPolicy() {}
}
