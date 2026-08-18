package com.lpf.adapter.in.web;

import com.lpf.adapter.config.TransactionalUseCases;
import com.lpf.adapter.in.security.CurrentUser;
import com.lpf.adapter.in.web.dto.CatalogItemResponse;
import com.lpf.adapter.in.web.dto.CategoryLookupResponse;
import com.lpf.adapter.in.web.dto.CreateCategoryRequest;
import com.lpf.adapter.in.web.dto.CreateLineItemRequest;
import com.lpf.adapter.in.web.dto.GroupLookupResponse;
import com.lpf.adapter.in.web.dto.UpdateLineItemRequest;
import com.lpf.application.port.out.CategoryGroupRepositoryPort;
import com.lpf.domain.exception.NotFoundException;
import com.lpf.domain.model.Category;
import com.lpf.domain.model.CategoryGroup;
import com.lpf.domain.model.LineItem;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CatalogController {

    private final TransactionalUseCases useCases;
    private final CategoryGroupRepositoryPort groups;

    public CatalogController(TransactionalUseCases useCases, CategoryGroupRepositoryPort groups) {
        this.useCases = useCases;
        this.groups = groups;
    }

    @GetMapping("/groups")
    public List<GroupLookupResponse> groups() {
        onboard();
        return groups.findByUserId(CurrentUser.id()).stream()
                .map(group -> new GroupLookupResponse(group.id(), group.name(), group.kind().name()))
                .toList();
    }

    @GetMapping("/line-items")
    public List<CatalogItemResponse> lineItems() {
        onboard();
        UUID userId = CurrentUser.id();
        Map<UUID, Category> categories = useCases.listCategories(userId).stream()
                .collect(Collectors.toMap(Category::id, Function.identity()));
        Map<UUID, CategoryGroup> groupMap = groups.findByUserId(userId).stream()
                .collect(Collectors.toMap(CategoryGroup::id, Function.identity()));
        return useCases.listLineItems(userId).stream()
                .map(item -> toResponse(item, categories, groupMap))
                .toList();
    }

    @GetMapping("/categories")
    public List<CategoryLookupResponse> categories() {
        onboard();
        UUID userId = CurrentUser.id();
        Map<UUID, CategoryGroup> groupMap = groups.findByUserId(userId).stream()
                .collect(Collectors.toMap(CategoryGroup::id, Function.identity()));
        return useCases.listCategories(userId).stream()
                .map(category -> {
                    CategoryGroup group = groupMap.get(category.groupId());
                    if (group == null) {
                        throw new NotFoundException("Grupo da categoria não encontrado");
                    }
                    return CategoryLookupResponse.from(category, group);
                })
                .toList();
    }

    @PostMapping("/categories")
    public CategoryLookupResponse createCategory(@RequestBody CreateCategoryRequest request) {
        onboard();
        UUID userId = CurrentUser.id();
        Category category = useCases.createCategory(userId, request.groupId(), request.name());
        CategoryGroup group = groups.findById(category.groupId())
                .orElseThrow(() -> new NotFoundException("Grupo não encontrado"));
        return CategoryLookupResponse.from(category, group);
    }

    @PostMapping("/line-items")
    public CatalogItemResponse createLineItem(@RequestBody CreateLineItemRequest request) {
        onboard();
        UUID userId = CurrentUser.id();
        LineItem item = useCases.createLineItem(userId, request.categoryId(), request.name());
        return lookup(userId, item);
    }

    @PatchMapping("/line-items/{id}")
    public CatalogItemResponse updateLineItem(@PathVariable UUID id, @RequestBody UpdateLineItemRequest request) {
        onboard();
        UUID userId = CurrentUser.id();
        LineItem item = useCases.updateLineItem(userId, id, request.name(), request.active());
        return lookup(userId, item);
    }

    private CatalogItemResponse lookup(UUID userId, LineItem item) {
        Map<UUID, Category> categories = useCases.listCategories(userId).stream()
                .collect(Collectors.toMap(Category::id, Function.identity()));
        Map<UUID, CategoryGroup> groupMap = groups.findByUserId(userId).stream()
                .collect(Collectors.toMap(CategoryGroup::id, Function.identity()));
        return toResponse(item, categories, groupMap);
    }

    private CatalogItemResponse toResponse(
            LineItem item,
            Map<UUID, Category> categories,
            Map<UUID, CategoryGroup> groupMap
    ) {
        Category category = categories.get(item.categoryId());
        if (category == null) {
            throw new NotFoundException("Categoria da linha não encontrada");
        }
        CategoryGroup group = groupMap.get(category.groupId());
        if (group == null) {
            throw new NotFoundException("Grupo da categoria não encontrado");
        }
        return CatalogItemResponse.from(item, category, group);
    }

    private void onboard() {
        useCases.ensureReady(CurrentUser.id(), CurrentUser.email(), CurrentUser.displayName());
    }
}
