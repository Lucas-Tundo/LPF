package com.lpf.adapter.out.persistence.jpa;

import com.lpf.adapter.out.persistence.entity.MonthlyEntryEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MonthlyEntryJpaRepository extends JpaRepository<MonthlyEntryEntity, UUID> {
    List<MonthlyEntryEntity> findByUserIdAndYearAndMonth(UUID userId, int year, int month);

    List<MonthlyEntryEntity> findByUserIdAndYear(UUID userId, int year);

    Optional<MonthlyEntryEntity> findByLineItemIdAndYearAndMonth(UUID lineItemId, int year, int month);

    boolean existsByUserIdAndYearAndMonth(UUID userId, int year, int month);
}
