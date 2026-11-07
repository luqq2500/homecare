package payment.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class SettlementCalculator {

    private SettlementCalculator() {}

    public static Settlement customerCancel(BigDecimal hoursFee, BigDecimal travelFee,
                                            int tierPercent, boolean afterStart) {
        BigDecimal total = hoursFee.add(travelFee);
        if (afterStart) {
            BigDecimal compensation = travelFee.add(hoursFee.multiply(new BigDecimal("0.30"))
                    .setScale(2, RoundingMode.HALF_UP));
            return Settlement.of(compensation, total.subtract(compensation), BigDecimal.ZERO);
        }
        BigDecimal fee = hoursFee.multiply(BigDecimal.valueOf(tierPercent))
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        return Settlement.of(fee, total.subtract(fee), BigDecimal.ZERO);
    }

    public static Settlement providerNoShow(BigDecimal hoursFee, BigDecimal travelFee) {
        BigDecimal total = hoursFee.add(travelFee);
        BigDecimal fine = hoursFee.multiply(new BigDecimal("0.30")).setScale(2, RoundingMode.HALF_UP);
        return Settlement.of(BigDecimal.ZERO, total, fine);
    }

    public static Settlement customerNoShow(BigDecimal hoursFee, BigDecimal travelFee) {
        BigDecimal total = hoursFee.add(travelFee);
        BigDecimal compensation = travelFee.add(hoursFee.multiply(new BigDecimal("0.30"))
                .setScale(2, RoundingMode.HALF_UP));
        return Settlement.of(compensation, total.subtract(compensation), BigDecimal.ZERO);
    }
}
