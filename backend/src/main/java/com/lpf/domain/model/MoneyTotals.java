package com.lpf.domain.model;

import java.math.BigDecimal;

public record MoneyTotals(
        BigDecimal forecast,
        BigDecimal paid,
        BigDecimal difference
) {
    public static MoneyTotals zero() {
        return new MoneyTotals(BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO);
    }
}
