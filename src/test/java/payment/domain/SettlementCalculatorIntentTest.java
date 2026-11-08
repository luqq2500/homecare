package payment.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class SettlementCalculatorIntentTest {

    private static final BigDecimal HOURS = new BigDecimal("200.00");
    private static final BigDecimal TRAVEL = new BigDecimal("15.00");

    @Test
    void customerCancelStandardBeforeStart() {
        Settlement s = SettlementCalculator.customerCancel(HOURS, TRAVEL, 10, false);
        assertTrue(s.toProvider().signum() > 0);
        assertTrue(s.toCustomer().signum() > 0);
        assertEquals(0, BigDecimal.ZERO.compareTo(s.toHoma()));
    }

    @Test
    void customerCancelAfterStartUsesNoShowCompensation() {
        Settlement s = SettlementCalculator.customerCancel(HOURS, TRAVEL, 10, true);
        assertTrue(s.toProvider().compareTo(s.toCustomer()) < 0);
        assertEquals(0, BigDecimal.ZERO.compareTo(s.toHoma()));
    }

    @Test
    void providerNoShowFullRefundAndFine() {
        Settlement s = SettlementCalculator.providerNoShow(HOURS, TRAVEL);
        assertEquals(0, BigDecimal.ZERO.compareTo(s.toProvider()));
        assertTrue(s.toHoma().signum() > 0);
    }

    @Test
    void customerNoShowCompensationToProvider() {
        Settlement s = SettlementCalculator.customerNoShow(HOURS, TRAVEL);
        assertTrue(s.toProvider().signum() > 0);
        assertEquals(0, BigDecimal.ZERO.compareTo(s.toHoma()));
    }

    @Test
    void conservationHolds() {
        Settlement s = SettlementCalculator.customerCancel(HOURS, TRAVEL, 10, false);
        BigDecimal total = s.toProvider().add(s.toCustomer()).add(s.toHoma());
        assertEquals(0, HOURS.add(TRAVEL).compareTo(total));
    }
}
