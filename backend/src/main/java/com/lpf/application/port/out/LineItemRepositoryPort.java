package com.lpf.application.port.out;

import com.lpf.domain.model.LineItem;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface LineItemRepositoryPort {
    List<LineItem> findByUserId(UUID userId);

    Optional<LineItem> findById(UUID id);

    LineItem save(LineItem item);

    int nextSortOrder(UUID userId, UUID categoryId);
}
