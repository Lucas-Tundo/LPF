package com.lpf.adapter.config;

import com.lpf.application.port.out.CategoryGroupRepositoryPort;
import com.lpf.application.port.out.CategoryRepositoryPort;
import com.lpf.application.port.out.LineItemRepositoryPort;
import com.lpf.application.port.out.MonthClosingRepositoryPort;
import com.lpf.application.port.out.MonthlyEntryRepositoryPort;
import com.lpf.application.port.out.ProfileRepositoryPort;
import com.lpf.application.service.DreApplicationService;
import com.lpf.application.service.OnboardingService;
import com.lpf.domain.service.DreCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfig {

    @Bean
    public DreCalculator dreCalculator() {
        return new DreCalculator();
    }

    @Bean
    public DreApplicationService dreApplicationService(
            CategoryGroupRepositoryPort groups,
            CategoryRepositoryPort categories,
            LineItemRepositoryPort lineItems,
            MonthlyEntryRepositoryPort entries,
            MonthClosingRepositoryPort closings,
            DreCalculator calculator
    ) {
        return new DreApplicationService(groups, categories, lineItems, entries, closings, calculator);
    }

    @Bean
    public OnboardingService onboardingService(
            ProfileRepositoryPort profiles,
            CategoryGroupRepositoryPort groups,
            CategoryRepositoryPort categories,
            LineItemRepositoryPort lineItems,
            MonthlyEntryRepositoryPort entries
    ) {
        return new OnboardingService(profiles, groups, categories, lineItems, entries);
    }
}
