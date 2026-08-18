package com.lpf.application.port.out;

import com.lpf.domain.model.Category;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CategoryRepositoryPort {
    List<Category> findByUserId(UUID userId);

    Optional<Category> findById(UUID id);

    Category save(Category category);

    int nextSortOrder(UUID userId, UUID groupId);
}
