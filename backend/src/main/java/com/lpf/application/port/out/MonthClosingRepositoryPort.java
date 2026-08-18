package com.lpf.application.port.out;

import com.lpf.domain.model.MonthClosing;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MonthClosingRepositoryPort {
    Optional<MonthClosing> findByUserIdAndYearAndMonth(UUID userId, int year, int month);

    List<MonthClosing> findByUserIdAndYear(UUID userId, int year);

    MonthClosing save(MonthClosing closing);
}
