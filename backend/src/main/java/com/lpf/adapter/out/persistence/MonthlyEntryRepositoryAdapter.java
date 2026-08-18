package com.lpf.adapter.out.persistence;

import com.lpf.adapter.out.persistence.jpa.MonthlyEntryJpaRepository;
import com.lpf.application.port.out.MonthlyEntryRepositoryPort;
import com.lpf.domain.model.MonthlyEntry;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MonthlyEntryRepositoryAdapter implements MonthlyEntryRepositoryPort {

    private final MonthlyEntryJpaRepository jpa;

    public MonthlyEntryRepositoryAdapter(MonthlyEntryJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<MonthlyEntry> findByUserIdAndYearAndMonth(UUID userId, int year, int month) {
        return jpa.findByUserIdAndYearAndMonth(userId, year, month).stream().map(PersistenceMapper::toDomain).toList();
    }

    @Override
    public List<MonthlyEntry> findByUserIdAndYear(UUID userId, int year) {
        return jpa.findByUserIdAndYear(userId, year).stream().map(PersistenceMapper::toDomain).toList();
    }

    @Override
    public Optional<MonthlyEntry> findByLineItemIdAndYearAndMonth(UUID lineItemId, int year, int month) {
        return jpa.findByLineItemIdAndYearAndMonth(lineItemId, year, month).map(PersistenceMapper::toDomain);
    }

    @Override
    public MonthlyEntry save(MonthlyEntry entry) {
        return PersistenceMapper.toDomain(jpa.save(PersistenceMapper.toEntity(entry)));
    }

    @Override
    public boolean existsByUserIdAndYearAndMonth(UUID userId, int year, int month) {
        return jpa.existsByUserIdAndYearAndMonth(userId, year, month);
    }
}
