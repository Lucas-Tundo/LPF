package com.lpf.adapter.out.persistence.jpa;

import com.lpf.adapter.out.persistence.entity.MonthClosingEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MonthClosingJpaRepository extends JpaRepository<MonthClosingEntity, UUID> {
    Optional<MonthClosingEntity> findByUserIdAndYearAndMonth(UUID userId, int year, int month);

    List<MonthClosingEntity> findByUserIdAndYear(UUID userId, int year);
}
