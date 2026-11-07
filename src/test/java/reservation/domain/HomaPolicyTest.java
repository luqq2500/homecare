package reservation.domain;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class HomaPolicyTest {

    @Test
    void policyConstantsMatchProductDescription() {
        assertEquals(15, HomaPolicy.CHECK_IN_PERIOD_MINUTES);
        assertEquals(300, HomaPolicy.CHECK_IN_RADIUS_METRES);
        assertEquals(15, HomaPolicy.CUSTOMER_NO_SHOW_WAIT_MINUTES);
        assertEquals(24, HomaPolicy.REQUEST_EXPIRY_HOURS);
        assertEquals(30, HomaPolicy.COMPLETION_REMINDER_MINUTES);
        assertEquals(7, HomaPolicy.EVIDENCE_RETENTION_DAYS);
        assertEquals(1, HomaPolicy.DISPUTE_WINDOW_EXTENSION_HOURS);
        assertEquals(24, HomaPolicy.FEE_WINDOW_HOURS);
        assertEquals(20, HomaPolicy.PROVIDER_LATE_CANCELLATION_FINE_PERCENT);
        assertEquals(30, HomaPolicy.PROVIDER_NO_SHOW_FINE_PERCENT);
        assertEquals(30, HomaPolicy.CUSTOMER_NO_SHOW_HOURS_PERCENT);
    }
}
