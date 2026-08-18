package com.lpf.domain.model;

import java.util.UUID;

public record LineItem(
        UUID id,
        UUID userId,
        UUID categoryId,
        String name,
        int sortOrder,
        boolean active
) {
}
