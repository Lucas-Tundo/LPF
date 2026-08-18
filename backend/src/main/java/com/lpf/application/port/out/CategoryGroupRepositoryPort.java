package com.lpf.application.port.out;

import com.lpf.domain.model.CategoryGroup;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryGroupRepositoryPort {
    List<CategoryGroup> findByUserId(UUID userId);

    Optional<CategoryGroup> findById(UUID id);

    CategoryGroup save(CategoryGroup group);

    boolean existsByUserId(UUID userId);
}
