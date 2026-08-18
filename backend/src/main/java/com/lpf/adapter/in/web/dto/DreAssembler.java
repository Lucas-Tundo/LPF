package com.lpf.adapter.in.web.dto;

import com.lpf.domain.model.DreComputedLine;
import com.lpf.domain.model.DreMonth;
import com.lpf.domain.model.DreTotals;
import com.lpf.domain.model.GroupKind;
import com.lpf.domain.model.MoneyTotals;
import com.lpf.domain.model.YearMonthTotals;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public final class DreAssembler {

    private DreAssembler() {
    }

    public static DreMonthResponse from(DreMonth month) {
        Map<UUID, GroupAccumulator> groups = new LinkedHashMap<>();
        for (DreComputedLine line : month.lines()) {
            GroupAccumulator group = groups.computeIfAbsent(line.groupId(), id -> new GroupAccumulator(
                    line.groupId(),
                    line.groupKind(),
                    line.groupName()
            ));
            CategoryAccumulator category = group.categories.computeIfAbsent(line.categoryId(), id ->
                    new CategoryAccumulator(line.categoryId(), line.categoryName()));
            category.lines.add(LineResponse.from(line));
        }
        DreTotals totals = month.totals();
        List<GroupResponse> groupResponses = new ArrayList<>();
        for (GroupAccumulator group : groups.values()) {
            MoneyTotals money = switch (group.kind) {
                case RECEITA -> totals.revenue();
                case DESP_FIXA -> totals.subtotalA();
                case DESP_VAR -> totals.subtotalB();
            };
            groupResponses.add(new GroupResponse(
                    group.id,
                    group.kind.name(),
                    group.name,
                    MoneyTotalsResponse.from(money),
                    group.categories.values().stream()
                            .map(category -> new CategoryResponse(category.id, category.name, category.lines))
                            .toList()
            ));
        }
        return new DreMonthResponse(month.year(), month.month(), groupResponses, TotalsResponse.from(totals));
    }

    public static List<YearMonthResponse> fromYear(List<YearMonthTotals> months) {
        return months.stream()
                .map(item -> new YearMonthResponse(item.month(), TotalsResponse.from(item.totals())))
                .toList();
    }

    private static final class GroupAccumulator {
        private final UUID id;
        private final GroupKind kind;
        private final String name;
        private final Map<UUID, CategoryAccumulator> categories = new LinkedHashMap<>();

        private GroupAccumulator(UUID id, GroupKind kind, String name) {
            this.id = id;
            this.kind = kind;
            this.name = name;
        }
    }

    private static final class CategoryAccumulator {
        private final UUID id;
        private final String name;
        private final List<LineResponse> lines = new ArrayList<>();

        private CategoryAccumulator(UUID id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    public record DreMonthResponse(
            int year,
            int month,
            List<GroupResponse> groups,
            TotalsResponse totals
    ) {
    }

    public record GroupResponse(
            UUID id,
            String kind,
            String name,
            MoneyTotalsResponse totals,
            List<CategoryResponse> categories
    ) {
    }

    public record CategoryResponse(
            UUID id,
            String name,
            List<LineResponse> lines
    ) {
    }

    public record LineResponse(
            UUID id,
            String name,
            String status,
            BigDecimal forecast,
            BigDecimal paidAmount,
            BigDecimal difference
    ) {
        static LineResponse from(DreComputedLine line) {
            return new LineResponse(
                    line.lineItemId(),
                    line.name(),
                    line.status().name(),
                    line.forecast(),
                    line.paidAmount(),
                    line.difference()
            );
        }
    }

    public record MoneyTotalsResponse(BigDecimal forecast, BigDecimal paid, BigDecimal difference) {
        static MoneyTotalsResponse from(MoneyTotals totals) {
            return new MoneyTotalsResponse(totals.forecast(), totals.paid(), totals.difference());
        }
    }

    public record TotalsResponse(
            MoneyTotalsResponse revenue,
            MoneyTotalsResponse subtotalA,
            MoneyTotalsResponse subtotalB,
            BigDecimal totalAbPaid,
            BigDecimal expectedRemaining,
            BigDecimal actualRemaining,
            BigDecimal closingDifference
    ) {
        static TotalsResponse from(DreTotals totals) {
            return new TotalsResponse(
                    MoneyTotalsResponse.from(totals.revenue()),
                    MoneyTotalsResponse.from(totals.subtotalA()),
                    MoneyTotalsResponse.from(totals.subtotalB()),
                    totals.totalAbPaid(),
                    totals.expectedRemaining(),
                    totals.actualRemaining(),
                    totals.closingDifference()
            );
        }
    }

    public record YearMonthResponse(int month, TotalsResponse totals) {
    }
}
