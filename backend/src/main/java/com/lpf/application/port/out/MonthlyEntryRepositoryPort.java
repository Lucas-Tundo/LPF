package com.lpf.application.port.out;

import com.lpf.domain.model.MonthlyEntry;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MonthlyEntryRepositoryPort {
    List<MonthlyEntry> findByUserIdAndYearAndMonth(UUID userId, int year, int month);

    List<MonthlyEntry> findByUserIdAndYear(UUID userId, int year);

    Optional<MonthlyEntry> findByLineItemIdAndYearAndMonth(UUID lineItemId, int year, int month);

    MonthlyEntry save(MonthlyEntry entry);

    boolean existsByUserIdAndYearAndMonth(UUID userId, int year, int month);
}
