package com.lpf.domain.model;

import java.util.UUID;

public record Category(
        UUID id,
        UUID userId,
        UUID groupId,
        String name,
        int sortOrder
) {
}
