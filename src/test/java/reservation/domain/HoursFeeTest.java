package reservation.domain;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class HoursFeeTest {

    @Test
    void of_calculatesCorrectly() {
        HoursFee fee = HoursFee.of(new BigDecimal("25.00"), 2, 3);
        assertEquals(0, new BigDecimal("150.00").compareTo(fee.amount()));
    }

    @Test
    void of_singleHelperOneHour() {
        HoursFee fee = HoursFee.of(new BigDecimal("40.00"), 1, 1);
        assertEquals(0, new BigDecimal("40.00").compareTo(fee.amount()));
    }

    @Test
    void of_fourHelpersFourHours() {
        HoursFee fee = HoursFee.of(new BigDecimal("15.50"), 4, 4);
        assertEquals(0, new BigDecimal("248.00").compareTo(fee.amount()));
    }
}
