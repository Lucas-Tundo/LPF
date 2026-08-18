package com.lpf.application.port.in;

import com.lpf.domain.model.DreMonth;
import com.lpf.domain.model.YearMonthTotals;

import java.util.List;
import java.util.UUID;

public interface LoadDreUseCase {
    DreMonth loadMonth(UUID userId, int year, int month);

    List<YearMonthTotals> loadYear(UUID userId, int year);
}
