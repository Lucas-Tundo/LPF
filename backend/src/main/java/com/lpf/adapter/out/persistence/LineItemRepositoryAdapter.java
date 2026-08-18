package com.lpf.adapter.out.persistence;

import com.lpf.adapter.out.persistence.jpa.LineItemJpaRepository;
import com.lpf.application.port.out.LineItemRepositoryPort;
import com.lpf.domain.model.LineItem;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class LineItemRepositoryAdapter implements LineItemRepositoryPort {

    private final LineItemJpaRepository jpa;

    public LineItemRepositoryAdapter(LineItemJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<LineItem> findByUserId(UUID userId) {
        return jpa.findByUserIdOrderBySortOrderAsc(userId).stream().map(PersistenceMapper::toDomain).toList();
    }

    @Override
    public Optional<LineItem> findById(UUID id) {
        return jpa.findById(id).map(PersistenceMapper::toDomain);
    }

    @Override
    public LineItem save(LineItem item) {
        return PersistenceMapper.toDomain(jpa.save(PersistenceMapper.toEntity(item)));
    }

    @Override
    public int nextSortOrder(UUID userId, UUID categoryId) {
        return jpa.findMaxSortOrder(userId, categoryId).orElse(0) + 10;
    }
}
