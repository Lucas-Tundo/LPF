package com.lpf.application.port.in;

import com.lpf.domain.model.DreMonth;

import java.math.BigDecimal;
import java.util.UUID;

public interface UpdateMonthlyEntryUseCase {
    DreMonth updateEntry(UUID userId, UUID lineItemId, int year, int month, BigDecimal forecast, BigDecimal paidAmount);
}
