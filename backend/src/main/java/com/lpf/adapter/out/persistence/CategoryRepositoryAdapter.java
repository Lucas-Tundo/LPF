package com.lpf.adapter.out.persistence;

import com.lpf.adapter.out.persistence.jpa.CategoryJpaRepository;
import com.lpf.application.port.out.CategoryRepositoryPort;
import com.lpf.domain.model.Category;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class CategoryRepositoryAdapter implements CategoryRepositoryPort {

    private final CategoryJpaRepository jpa;

    public CategoryRepositoryAdapter(CategoryJpaRepository jpa) {
        this.jpa = jpa;
    }

    @Override
    public List<Category> findByUserId(UUID userId) {
        return jpa.findByUserIdOrderBySortOrderAsc(userId).stream().map(PersistenceMapper::toDomain).toList();
    }

    @Override
    public Optional<Category> findById(UUID id) {
        return jpa.findById(id).map(PersistenceMapper::toDomain);
    }

    @Override
    public Category save(Category category) {
        return PersistenceMapper.toDomain(jpa.save(PersistenceMapper.toEntity(category)));
    }

    @Override
    public int nextSortOrder(UUID userId, UUID groupId) {
        return jpa.findMaxSortOrder(userId, groupId).orElse(0) + 10;
    }
}
