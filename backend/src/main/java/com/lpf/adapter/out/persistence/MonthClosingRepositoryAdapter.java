package com.lpf.adapter.out.persistence;

import com.lpf.adapter.out.persistence.jpa.MonthClosingJpaRepository;
import com.lpf.application.port.out.MonthClosingRepositoryPort;
import com.lpf.domain.model.MonthClosing;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class MonthClosingRepositoryAdapter implements MonthClosingRepositoryPort {

    private final MonthClosingJpaRepository jpa;

    public MonthClosingRepositoryAdapter(MonthClosingJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public Optional<MonthClosing> findByUserIdAndYearAndMonth(UUID userId, int year, int month) {
        return jpa.findByUserIdAndYearAndMonth(userId, year, month).map(PersistenceMapper::toDomain);
    }

    @Override
    public List<MonthClosing> findByUserIdAndYear(UUID userId, int year) {
        return jpa.findByUserIdAndYear(userId, year).stream().map(PersistenceMapper::toDomain).toList();
    }

    @Override
    public MonthClosing save(MonthClosing closing) {
        return PersistenceMapper.toDomain(jpa.save(PersistenceMapper.toEntity(closing)));
    }
}
