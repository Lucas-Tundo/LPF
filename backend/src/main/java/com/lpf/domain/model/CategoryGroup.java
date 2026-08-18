package com.lpf.domain.model;

import java.util.UUID;

public record CategoryGroup(
        UUID id,
        UUID userId,
        GroupKind kind,
        String name,
        int sortOrder
) {
}
