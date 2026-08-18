package com.lpf.domain.service;

import com.lpf.domain.model.DreComputedLine;
import com.lpf.domain.model.DreLineSnapshot;
import com.lpf.domain.model.DreMonth;
import com.lpf.domain.model.DreTotals;
import com.lpf.domain.model.GroupKind;
import com.lpf.domain.model.MoneyTotals;
import com.lpf.domain.model.PaymentStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

public class DreCalculator {

    private static final int SCALE = 2;

    public PaymentStatus status(BigDecimal paidAmount) {
        return paidAmount != null ? PaymentStatus.PG : PaymentStatus.ABERTO;
    }

    public BigDecimal difference(BigDecimal forecast, BigDecimal paidAmount) {
        if (paidAmount == null) {
            return null;
        }
        return money(paidAmount).subtract(money(forecast));
    }

    public DreComputedLine computeLine(DreLineSnapshot snapshot) {
        PaymentStatus paymentStatus = status(snapshot.paidAmount());
        return new DreComputedLine(
                snapshot.lineItemId(),
                snapshot.categoryId(),
                snapshot.categoryName(),
                snapshot.categorySortOrder(),
                snapshot.groupId(),
                snapshot.groupKind(),
                snapshot.groupName(),
                snapshot.groupSortOrder(),
                snapshot.name(),
                snapshot.sortOrder(),
                money(snapshot.forecast()),
                snapshot.paidAmount() == null ? null : money(snapshot.paidAmount()),
                paymentStatus,
                difference(snapshot.forecast(), snapshot.paidAmount())
        );
    }

    public DreMonth month(int year, int month, List<DreLineSnapshot> snapshots, BigDecimal actualRemaining) {
        List<DreComputedLine> lines = new ArrayList<>();
        for (DreLineSnapshot snapshot : snapshots) {
            lines.add(computeLine(snapshot));
        }
        return new DreMonth(year, month, List.copyOf(lines), totals(lines, actualRemaining));
    }

    public DreTotals totals(List<DreComputedLine> lines, BigDecimal actualRemaining) {
        MoneyTotals revenue = sum(lines, GroupKind.RECEITA);
        MoneyTotals subtotalA = sum(lines, GroupKind.DESP_FIXA);
        MoneyTotals subtotalB = sum(lines, GroupKind.DESP_VAR);
        BigDecimal totalAbPaid = money(subtotalA.paid()).add(money(subtotalB.paid()));
        BigDecimal expectedRemaining = money(revenue.paid()).subtract(totalAbPaid);
        BigDecimal remaining = actualRemaining == null ? null : money(actualRemaining);
        BigDecimal closingDifference = remaining == null ? null : remaining.subtract(expectedRemaining);
        return new DreTotals(
                revenue,
                subtotalA,
                subtotalB,
                totalAbPaid,
                expectedRemaining,
                remaining,
                closingDifference
        );
    }

    private MoneyTotals sum(List<DreComputedLine> lines, GroupKind kind) {
        BigDecimal forecast = BigDecimal.ZERO;
        BigDecimal paid = BigDecimal.ZERO;
        BigDecimal difference = BigDecimal.ZERO;
        for (DreComputedLine line : lines) {
            if (line.groupKind() != kind) {
                continue;
            }
            forecast = forecast.add(money(line.forecast()));
            paid = paid.add(money(line.paidAmount()));
            difference = difference.add(money(line.difference()));
        }
        return new MoneyTotals(forecast, paid, difference);
    }

    private BigDecimal money(BigDecimal value) {
        if (value == null) {
            return BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP);
        }
        return value.setScale(SCALE, RoundingMode.HALF_UP);
    }
}
