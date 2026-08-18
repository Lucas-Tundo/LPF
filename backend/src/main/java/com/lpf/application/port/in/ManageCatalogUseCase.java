package com.lpf.application.port.in;

import com.lpf.domain.model.Category;
import com.lpf.domain.model.LineItem;

import java.util.List;
import java.util.UUID;

public interface ManageCatalogUseCase {
    List<LineItem> listLineItems(UUID userId);

    List<Category> listCategories(UUID userId);

    Category createCategory(UUID userId, UUID groupId, String name);

    LineItem createLineItem(UUID userId, UUID categoryId, String name);

    LineItem updateLineItem(UUID userId, UUID lineItemId, String name, Boolean active);
}
