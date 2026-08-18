package com.lpf.adapter.config;

import com.lpf.application.service.DreApplicationService;
import com.lpf.application.service.OnboardingService;
import com.lpf.domain.model.Category;
import com.lpf.domain.model.DreMonth;
import com.lpf.domain.model.LineItem;
import com.lpf.domain.model.YearMonthTotals;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
public class TransactionalUseCases {

    private final OnboardingService onboarding;
    private final DreApplicationService dre;

    public TransactionalUseCases(OnboardingService onboarding, DreApplicationService dre) {
        this.onboarding = onboarding;
        this.dre = dre;
    }

    @Transactional
    public void ensureReady(UUID userId, String email, String displayName) {
        onboarding.ensureReady(userId, email, displayName);
    }

    @Transactional(readOnly = true)
    public DreMonth loadMonth(UUID userId, int year, int month) {
        return dre.loadMonth(userId, year, month);
    }

    @Transactional(readOnly = true)
    public List<YearMonthTotals> loadYear(UUID userId, int year) {
        return dre.loadYear(userId, year);
    }

    @Transactional
    public DreMonth updateEntry(
            UUID userId,
            UUID lineItemId,
            int year,
            int month,
            BigDecimal forecast,
            BigDecimal paidAmount
    ) {
        return dre.updateEntry(userId, lineItemId, year, month, forecast, paidAmount);
    }

    @Transactional
    public DreMonth updateClosing(UUID userId, int year, int month, BigDecimal actualRemaining) {
        return dre.updateClosing(userId, year, month, actualRemaining);
    }

    @Transactional(readOnly = true)
    public List<LineItem> listLineItems(UUID userId) {
        return dre.listLineItems(userId);
    }

    @Transactional(readOnly = true)
    public List<Category> listCategories(UUID userId) {
        return dre.listCategories(userId);
    }

    @Transactional
    public Category createCategory(UUID userId, UUID groupId, String name) {
        return dre.createCategory(userId, groupId, name);
    }

    @Transactional
    public LineItem createLineItem(UUID userId, UUID categoryId, String name) {
        return dre.createLineItem(userId, categoryId, name);
    }

    @Transactional
    public LineItem updateLineItem(UUID userId, UUID lineItemId, String name, Boolean active) {
        return dre.updateLineItem(userId, lineItemId, name, active);
    }
}
