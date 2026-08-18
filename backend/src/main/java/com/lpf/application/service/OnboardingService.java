package com.lpf.application.service;

import com.lpf.application.port.in.OnboardUserUseCase;
import com.lpf.application.port.out.CategoryGroupRepositoryPort;
import com.lpf.application.port.out.CategoryRepositoryPort;
import com.lpf.application.port.out.LineItemRepositoryPort;
import com.lpf.application.port.out.MonthlyEntryRepositoryPort;
import com.lpf.application.port.out.ProfileRepositoryPort;
import com.lpf.domain.catalog.DefaultCatalog;
import com.lpf.domain.model.Category;
import com.lpf.domain.model.CategoryGroup;
import com.lpf.domain.model.LineItem;
import com.lpf.domain.model.MonthlyEntry;
import com.lpf.domain.model.Profile;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class OnboardingService implements OnboardUserUseCase {

    static final int SEED_YEAR = 2026;
    static final int SEED_MONTH = 8;

    private final ProfileRepositoryPort profiles;
    private final CategoryGroupRepositoryPort groups;
    private final CategoryRepositoryPort categories;
    private final LineItemRepositoryPort lineItems;
    private final MonthlyEntryRepositoryPort entries;

    public OnboardingService(
            ProfileRepositoryPort profiles,
            CategoryGroupRepositoryPort groups,
            CategoryRepositoryPort categories,
            LineItemRepositoryPort lineItems,
            MonthlyEntryRepositoryPort entries
    ) {
        this.profiles = profiles;
        this.groups = groups;
        this.categories = categories;
        this.lineItems = lineItems;
        this.entries = entries;
    }

    @Override
    public void ensureReady(UUID userId, String email, String displayName) {
        Profile profile = profiles.findById(userId).orElse(null);
        if (profile == null) {
            profile = profiles.save(new Profile(userId, email, displayName, Instant.now()));
        }
        if (!groups.existsByUserId(userId)) {
            seedCatalog(profile.id());
        }
        if (!entries.existsByUserIdAndYearAndMonth(userId, SEED_YEAR, SEED_MONTH)) {
            seedAugustForecasts(userId);
        }
    }

    private void seedCatalog(UUID userId) {
        Map<String, CategoryGroup> groupByKey = new HashMap<>();
        Map<String, Category> categoryByKey = new HashMap<>();
        for (DefaultCatalog.SeedLine seed : DefaultCatalog.lines()) {
            String groupKey = seed.groupKind().name();
            CategoryGroup group = groupByKey.get(groupKey);
            if (group == null) {
                group = groups.save(new CategoryGroup(
                        UUID.randomUUID(),
                        userId,
                        seed.groupKind(),
                        seed.groupName(),
                        seed.groupSort()
                ));
                groupByKey.put(groupKey, group);
            }
            String categoryKey = groupKey + "::" + seed.categoryName();
            Category category = categoryByKey.get(categoryKey);
            if (category == null) {
                category = categories.save(new Category(
                        UUID.randomUUID(),
                        userId,
                        group.id(),
                        seed.categoryName(),
                        seed.categorySort()
                ));
                categoryByKey.put(categoryKey, category);
            }
            if (seed.lineName() != null) {
                lineItems.save(new LineItem(
                        UUID.randomUUID(),
                        userId,
                        category.id(),
                        seed.lineName(),
                        seed.lineSort(),
                        true
                ));
            }
        }
    }

    private void seedAugustForecasts(UUID userId) {
        Map<String, BigDecimal> forecasts = DefaultCatalog.august2026Forecasts();
        for (LineItem item : lineItems.findByUserId(userId)) {
            BigDecimal forecast = forecasts.getOrDefault(item.name(), BigDecimal.ZERO);
            entries.save(new MonthlyEntry(
                    UUID.randomUUID(),
                    userId,
                    item.id(),
                    SEED_YEAR,
                    SEED_MONTH,
                    forecast,
                    null
            ));
        }
    }
}
