package com.lpf.application.service;

import com.lpf.application.port.in.LoadDreUseCase;
import com.lpf.application.port.in.ManageCatalogUseCase;
import com.lpf.application.port.in.UpdateMonthClosingUseCase;
import com.lpf.application.port.in.UpdateMonthlyEntryUseCase;
import com.lpf.application.port.out.CategoryGroupRepositoryPort;
import com.lpf.application.port.out.CategoryRepositoryPort;
import com.lpf.application.port.out.LineItemRepositoryPort;
import com.lpf.application.port.out.MonthClosingRepositoryPort;
import com.lpf.application.port.out.MonthlyEntryRepositoryPort;
import com.lpf.domain.exception.ForbiddenException;
import com.lpf.domain.exception.NotFoundException;
import com.lpf.domain.model.Category;
import com.lpf.domain.model.CategoryGroup;
import com.lpf.domain.model.DreLineSnapshot;
import com.lpf.domain.model.DreMonth;
import com.lpf.domain.model.LineItem;
import com.lpf.domain.model.MonthClosing;
import com.lpf.domain.model.MonthlyEntry;
import com.lpf.domain.model.YearMonthTotals;
import com.lpf.domain.service.DreCalculator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class DreApplicationService implements
        LoadDreUseCase,
        UpdateMonthlyEntryUseCase,
        UpdateMonthClosingUseCase,
        ManageCatalogUseCase {

    private final CategoryGroupRepositoryPort groups;
    private final CategoryRepositoryPort categories;
    private final LineItemRepositoryPort lineItems;
    private final MonthlyEntryRepositoryPort entries;
    private final MonthClosingRepositoryPort closings;
    private final DreCalculator calculator;

    public DreApplicationService(
            CategoryGroupRepositoryPort groups,
            CategoryRepositoryPort categories,
            LineItemRepositoryPort lineItems,
            MonthlyEntryRepositoryPort entries,
            MonthClosingRepositoryPort closings,
            DreCalculator calculator
    ) {
        this.groups = groups;
        this.categories = categories;
        this.lineItems = lineItems;
        this.entries = entries;
        this.closings = closings;
        this.calculator = calculator;
    }

    @Override
    public DreMonth loadMonth(UUID userId, int year, int month) {
        validatePeriod(year, month);
        BigDecimal actual = closings.findByUserIdAndYearAndMonth(userId, year, month)
                .map(MonthClosing::actualRemaining)
                .orElse(null);
        return calculator.month(year, month, snapshots(userId, year, month), actual);
    }

    @Override
    public List<YearMonthTotals> loadYear(UUID userId, int year) {
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("Ano inválido");
        }
        Map<Integer, List<DreLineSnapshot>> byMonth = new HashMap<>();
        for (int month = 1; month <= 12; month++) {
            byMonth.put(month, snapshots(userId, year, month));
        }
        Map<Integer, BigDecimal> remaining = new HashMap<>();
        for (MonthClosing closing : closings.findByUserIdAndYear(userId, year)) {
            remaining.put(closing.month(), closing.actualRemaining());
        }
        List<YearMonthTotals> result = new ArrayList<>();
        for (int month = 1; month <= 12; month++) {
            DreMonth dreMonth = calculator.month(year, month, byMonth.get(month), remaining.get(month));
            result.add(new YearMonthTotals(month, dreMonth.totals()));
        }
        return List.copyOf(result);
    }

    @Override
    public DreMonth updateEntry(
            UUID userId,
            UUID lineItemId,
            int year,
            int month,
            BigDecimal forecast,
            BigDecimal paidAmount
    ) {
        validatePeriod(year, month);
        LineItem item = requireOwnedLineItem(userId, lineItemId);
        MonthlyEntry existing = entries.findByLineItemIdAndYearAndMonth(lineItemId, year, month)
                .orElse(new MonthlyEntry(UUID.randomUUID(), userId, item.id(), year, month, BigDecimal.ZERO, null));
        MonthlyEntry updated = new MonthlyEntry(
                existing.id(),
                userId,
                item.id(),
                year,
                month,
                forecast == null ? BigDecimal.ZERO : forecast,
                paidAmount
        );
        entries.save(updated);
        return loadMonth(userId, year, month);
    }

    @Override
    public DreMonth updateClosing(UUID userId, int year, int month, BigDecimal actualRemaining) {
        validatePeriod(year, month);
        MonthClosing existing = closings.findByUserIdAndYearAndMonth(userId, year, month)
                .orElse(new MonthClosing(UUID.randomUUID(), userId, year, month, null));
        closings.save(new MonthClosing(existing.id(), userId, year, month, actualRemaining));
        return loadMonth(userId, year, month);
    }

    @Override
    public List<LineItem> listLineItems(UUID userId) {
        List<LineItem> items = new ArrayList<>(lineItems.findByUserId(userId));
        items.sort(Comparator.comparingInt(LineItem::sortOrder).thenComparing(LineItem::name));
        return List.copyOf(items);
    }

    @Override
    public List<Category> listCategories(UUID userId) {
        List<Category> list = new ArrayList<>(categories.findByUserId(userId));
        list.sort(Comparator.comparingInt(Category::sortOrder).thenComparing(Category::name));
        return List.copyOf(list);
    }

    @Override
    public Category createCategory(UUID userId, UUID groupId, String name) {
        CategoryGroup group = groups.findById(groupId)
                .orElseThrow(() -> new NotFoundException("Grupo não encontrado"));
        if (!group.userId().equals(userId)) {
            throw new ForbiddenException("Grupo não pertence ao usuário");
        }
        String trimmed = requireName(name);
        int sort = categories.nextSortOrder(userId, groupId);
        return categories.save(new Category(UUID.randomUUID(), userId, groupId, trimmed, sort));
    }

    @Override
    public LineItem createLineItem(UUID userId, UUID categoryId, String name) {
        Category category = categories.findById(categoryId)
                .orElseThrow(() -> new NotFoundException("Categoria não encontrada"));
        if (!category.userId().equals(userId)) {
            throw new ForbiddenException("Categoria não pertence ao usuário");
        }
        String trimmed = requireName(name);
        int sort = lineItems.nextSortOrder(userId, categoryId);
        return lineItems.save(new LineItem(UUID.randomUUID(), userId, categoryId, trimmed, sort, true));
    }

    @Override
    public LineItem updateLineItem(UUID userId, UUID lineItemId, String name, Boolean active) {
        LineItem item = requireOwnedLineItem(userId, lineItemId);
        String nextName = name == null ? item.name() : requireName(name);
        boolean nextActive = active == null ? item.active() : active;
        return lineItems.save(new LineItem(item.id(), userId, item.categoryId(), nextName, item.sortOrder(), nextActive));
    }

    private List<DreLineSnapshot> snapshots(UUID userId, int year, int month) {
        Map<UUID, CategoryGroup> groupById = new HashMap<>();
        for (CategoryGroup group : groups.findByUserId(userId)) {
            groupById.put(group.id(), group);
        }
        Map<UUID, Category> categoryById = new HashMap<>();
        for (Category category : categories.findByUserId(userId)) {
            categoryById.put(category.id(), category);
        }
        Map<UUID, MonthlyEntry> entryByLine = new HashMap<>();
        for (MonthlyEntry entry : entries.findByUserIdAndYearAndMonth(userId, year, month)) {
            entryByLine.put(entry.lineItemId(), entry);
        }

        List<DreLineSnapshot> snapshots = new ArrayList<>();
        for (LineItem item : lineItems.findByUserId(userId)) {
            if (!item.active()) {
                continue;
            }
            Category category = categoryById.get(item.categoryId());
            if (category == null) {
                continue;
            }
            CategoryGroup group = groupById.get(category.groupId());
            if (group == null) {
                continue;
            }
            MonthlyEntry entry = entryByLine.get(item.id());
            BigDecimal forecast = entry == null || entry.forecast() == null ? BigDecimal.ZERO : entry.forecast();
            BigDecimal paid = entry == null ? null : entry.paidAmount();
            snapshots.add(new DreLineSnapshot(
                    item.id(),
                    category.id(),
                    category.name(),
                    category.sortOrder(),
                    group.id(),
                    group.kind(),
                    group.name(),
                    group.sortOrder(),
                    item.name(),
                    item.sortOrder(),
                    forecast,
                    paid
            ));
        }
        snapshots.sort(Comparator
                .comparingInt(DreLineSnapshot::groupSortOrder)
                .thenComparingInt(DreLineSnapshot::categorySortOrder)
                .thenComparingInt(DreLineSnapshot::sortOrder)
                .thenComparing(DreLineSnapshot::name));
        return snapshots;
    }

    private LineItem requireOwnedLineItem(UUID userId, UUID lineItemId) {
        LineItem item = lineItems.findById(lineItemId)
                .orElseThrow(() -> new NotFoundException("Linha não encontrada"));
        if (!item.userId().equals(userId)) {
            throw new ForbiddenException("Linha não pertence ao usuário");
        }
        return item;
    }

    private void validatePeriod(int year, int month) {
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("Ano inválido");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Mês inválido");
        }
    }

    private String requireName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Nome é obrigatório");
        }
        return name.trim();
    }
}
