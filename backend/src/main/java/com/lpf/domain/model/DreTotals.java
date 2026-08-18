package com.lpf.domain.model;

import java.math.BigDecimal;

public record DreTotals(
        MoneyTotals revenue,
        MoneyTotals subtotalA,
        MoneyTotals subtotalB,
        BigDecimal totalAbPaid,
        BigDecimal expectedRemaining,
        BigDecimal actualRemaining,
        BigDecimal closingDifference
) {
}
