package com.lpf.adapter.in.web.dto;

import com.lpf.domain.model.Category;
import com.lpf.domain.model.CategoryGroup;

import java.util.UUID;

public record CategoryLookupResponse(
        UUID id,
        UUID groupId,
        String groupName,
        String groupKind,
        String name
) {
    public static CategoryLookupResponse from(Category category, CategoryGroup group) {
        return new CategoryLookupResponse(
                category.id(),
                group.id(),
                group.name(),
                group.kind().name(),
                category.name()
        );
    }
}
