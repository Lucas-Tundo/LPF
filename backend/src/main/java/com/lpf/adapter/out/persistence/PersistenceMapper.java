package com.lpf.adapter.out.persistence;

import com.lpf.adapter.out.persistence.entity.CategoryEntity;
import com.lpf.adapter.out.persistence.entity.CategoryGroupEntity;
import com.lpf.adapter.out.persistence.entity.LineItemEntity;
import com.lpf.adapter.out.persistence.entity.MonthClosingEntity;
import com.lpf.adapter.out.persistence.entity.MonthlyEntryEntity;
import com.lpf.adapter.out.persistence.entity.ProfileEntity;
import com.lpf.domain.model.Category;
import com.lpf.domain.model.CategoryGroup;
import com.lpf.domain.model.GroupKind;
import com.lpf.domain.model.LineItem;
import com.lpf.domain.model.MonthClosing;
import com.lpf.domain.model.MonthlyEntry;
import com.lpf.domain.model.Profile;

public final class PersistenceMapper {

    private PersistenceMapper() {
    }

    public static Profile toDomain(ProfileEntity entity) {
        return new Profile(entity.getId(), entity.getEmail(), entity.getDisplayName(), entity.getOnboardedAt());
    }

    public static ProfileEntity toEntity(Profile profile) {
        ProfileEntity entity = new ProfileEntity();
        entity.setId(profile.id());
        entity.setEmail(profile.email());
        entity.setDisplayName(profile.displayName());
        entity.setOnboardedAt(profile.onboardedAt());
        return entity;
    }

    public static CategoryGroup toDomain(CategoryGroupEntity entity) {
        return new CategoryGroup(
                entity.getId(),
                entity.getUserId(),
                GroupKind.from(entity.getKind()),
                entity.getName(),
                entity.getSortOrder()
        );
    }

    public static CategoryGroupEntity toEntity(CategoryGroup group) {
        CategoryGroupEntity entity = new CategoryGroupEntity();
        entity.setId(group.id());
        entity.setUserId(group.userId());
        entity.setKind(group.kind().name());
        entity.setName(group.name());
        entity.setSortOrder(group.sortOrder());
        return entity;
    }

    public static Category toDomain(CategoryEntity entity) {
        return new Category(entity.getId(), entity.getUserId(), entity.getGroupId(), entity.getName(), entity.getSortOrder());
    }

    public static CategoryEntity toEntity(Category category) {
        CategoryEntity entity = new CategoryEntity();
        entity.setId(category.id());
        entity.setUserId(category.userId());
        entity.setGroupId(category.groupId());
        entity.setName(category.name());
        entity.setSortOrder(category.sortOrder());
        return entity;
    }

    public static LineItem toDomain(LineItemEntity entity) {
        return new LineItem(
                entity.getId(),
                entity.getUserId(),
                entity.getCategoryId(),
                entity.getName(),
                entity.getSortOrder(),
                entity.isActive()
        );
    }

    public static LineItemEntity toEntity(LineItem item) {
        LineItemEntity entity = new LineItemEntity();
        entity.setId(item.id());
        entity.setUserId(item.userId());
        entity.setCategoryId(item.categoryId());
        entity.setName(item.name());
        entity.setSortOrder(item.sortOrder());
        entity.setActive(item.active());
        return entity;
    }

    public static MonthlyEntry toDomain(MonthlyEntryEntity entity) {
        return new MonthlyEntry(
                entity.getId(),
                entity.getUserId(),
                entity.getLineItemId(),
                entity.getYear(),
                entity.getMonth(),
                entity.getForecast(),
                entity.getPaidAmount()
        );
    }

    public static MonthlyEntryEntity toEntity(MonthlyEntry entry) {
        MonthlyEntryEntity entity = new MonthlyEntryEntity();
        entity.setId(entry.id());
        entity.setUserId(entry.userId());
        entity.setLineItemId(entry.lineItemId());
        entity.setYear(entry.year());
        entity.setMonth(entry.month());
        entity.setForecast(entry.forecast());
        entity.setPaidAmount(entry.paidAmount());
        return entity;
    }

    public static MonthClosing toDomain(MonthClosingEntity entity) {
        return new MonthClosing(
                entity.getId(),
                entity.getUserId(),
                entity.getYear(),
                entity.getMonth(),
                entity.getActualRemaining()
        );
    }

    public static MonthClosingEntity toEntity(MonthClosing closing) {
        MonthClosingEntity entity = new MonthClosingEntity();
        entity.setId(closing.id());
        entity.setUserId(closing.userId());
        entity.setYear(closing.year());
        entity.setMonth(closing.month());
        entity.setActualRemaining(closing.actualRemaining());
        return entity;
    }
}
