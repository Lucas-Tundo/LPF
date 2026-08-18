package com.lpf.application.port.in;

import com.lpf.domain.model.DreMonth;

import java.math.BigDecimal;
import java.util.UUID;

public interface UpdateMonthClosingUseCase {
    DreMonth updateClosing(UUID userId, int year, int month, BigDecimal actualRemaining);
}
