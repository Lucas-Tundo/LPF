package com.lpf.adapter.in.web.dto;

import com.lpf.domain.model.Category;
import com.lpf.domain.model.CategoryGroup;
import com.lpf.domain.model.LineItem;

import java.util.UUID;

public record CatalogItemResponse(
        UUID id,
        UUID groupId,
        String groupName,
        String groupKind,
        UUID categoryId,
        String categoryName,
        String name,
        boolean active,
        int sortOrder
) {
    public static CatalogItemResponse from(LineItem item, Category category, CategoryGroup group) {
        return new CatalogItemResponse(
                item.id(),
                group.id(),
                group.name(),
                group.kind().name(),
                category.id(),
                category.name(),
                item.name(),
                item.active(),
                item.sortOrder()
        );
    }
}
