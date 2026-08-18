package com.lpf.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record MonthlyEntry(
        UUID id,
        UUID userId,
        UUID lineItemId,
        int year,
        int month,
        BigDecimal forecast,
        BigDecimal paidAmount
) {
}
