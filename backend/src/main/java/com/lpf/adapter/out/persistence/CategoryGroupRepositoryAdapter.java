package com.lpf.adapter.out.persistence;

import com.lpf.adapter.out.persistence.jpa.CategoryGroupJpaRepository;
import com.lpf.application.port.out.CategoryGroupRepositoryPort;
import com.lpf.domain.model.CategoryGroup;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CategoryGroupRepositoryAdapter implements CategoryGroupRepositoryPort {

    private final CategoryGroupJpaRepository jpa;

    public CategoryGroupRepositoryAdapter(CategoryGroupJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<CategoryGroup> findByUserId(UUID userId) {
        return jpa.findByUserIdOrderBySortOrderAsc(userId).stream().map(PersistenceMapper::toDomain).toList();
    }

    @Override
    public Optional<CategoryGroup> findById(UUID id) {
        return jpa.findById(id).map(PersistenceMapper::toDomain);
    }

    @Override
    public CategoryGroup save(CategoryGroup group) {
        return PersistenceMapper.toDomain(jpa.save(PersistenceMapper.toEntity(group)));
    }

    @Override
    public boolean existsByUserId(UUID userId) {
        return jpa.existsByUserId(userId);
    }
}
