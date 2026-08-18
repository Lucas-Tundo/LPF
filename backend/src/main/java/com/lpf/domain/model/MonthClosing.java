package com.lpf.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record MonthClosing(
        UUID id,
        UUID userId,
        int year,
        int month,
        BigDecimal actualRemaining
) {
}
