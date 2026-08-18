package com.lpf.domain.service;

import com.lpf.domain.model.DreComputedLine;
import com.lpf.domain.model.DreLineSnapshot;
import com.lpf.domain.model.DreMonth;
import com.lpf.domain.model.DreTotals;
import com.lpf.domain.model.GroupKind;
import com.lpf.domain.model.PaymentStatus;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DreCalculatorTest {

    private final DreCalculator calculator = new DreCalculator();

    @Test
    void paidLineIsPgAndDifferenceIsPaidMinusForecast() {
        DreComputedLine line = calculator.computeLine(snapshot(
                GroupKind.DESP_FIXA,
                "ALUGUEL",
                "2170.00",
                "2200.00"
        ));
        assertEquals(PaymentStatus.PG, line.status());
        assertEquals(new BigDecimal("30.00"), line.difference());
    }

    @Test
    void unpaidLineIsAbertoAndHasNoDifference() {
        DreComputedLine line = calculator.computeLine(snapshot(
                GroupKind.RECEITA,
                "SALÁRIO",
                "4500.00",
                null
        ));
        assertEquals(PaymentStatus.ABERTO, line.status());
        assertNull(line.difference());
    }

    @Test
    void augustForecastsReproduceSpreadsheetTotalsWhenNothingIsPaid() {
        List<DreLineSnapshot> lines = List.of(
                snapshot(GroupKind.RECEITA, "SALDO ANTERIOR", "200.00", null),
                snapshot(GroupKind.RECEITA, "SALÁRIO", "4500.00", null),
                snapshot(GroupKind.RECEITA, "MESA 1", "216.00", null),
                snapshot(GroupKind.RECEITA, "MESA 2", "120.00", null),
                snapshot(GroupKind.RECEITA, "MESA 3", "60.00", null),
                snapshot(GroupKind.RECEITA, "REEMBOLSO IAM", "593.75", null),
                snapshot(GroupKind.DESP_FIXA, "ALUGUEL", "2170.00", null),
                snapshot(GroupKind.DESP_FIXA, "INTERNET", "100.00", null),
                snapshot(GroupKind.DESP_FIXA, "cursos", "218.19", null),
                snapshot(GroupKind.DESP_FIXA, "MEI", "86.90", null),
                snapshot(GroupKind.DESP_VAR, "CARTÃO MP", "290.67", null),
                snapshot(GroupKind.DESP_VAR, "CARTÃO ITAU", "1705.57", null),
                snapshot(GroupKind.DESP_VAR, "Lavanderia", "30.00", null)
        );
        DreMonth month = calculator.month(2026, 8, lines, null);
        DreTotals totals = month.totals();
        assertEquals(new BigDecimal("5689.75"), totals.revenue().forecast());
        assertEquals(new BigDecimal("2575.09"), totals.subtotalA().forecast());
        assertEquals(new BigDecimal("2026.24"), totals.subtotalB().forecast());
        assertEquals(new BigDecimal("0.00"), totals.totalAbPaid());
        assertEquals(new BigDecimal("0.00"), totals.expectedRemaining());
        assertNull(totals.actualRemaining());
        assertNull(totals.closingDifference());
    }

    @Test
    void closingUsesPaidRevenueMinusExpensesAndManualRemaining() {
        List<DreLineSnapshot> lines = List.of(
                snapshot(GroupKind.RECEITA, "SALÁRIO", "4500.00", "4500.00"),
                snapshot(GroupKind.DESP_FIXA, "ALUGUEL", "2170.00", "2170.00"),
                snapshot(GroupKind.DESP_VAR, "MERCADO", "800.00", "900.00")
        );
        DreMonth month = calculator.month(2026, 8, lines, new BigDecimal("1300.00"));
        DreTotals totals = month.totals();
        assertEquals(new BigDecimal("3070.00"), totals.totalAbPaid());
        assertEquals(new BigDecimal("1430.00"), totals.expectedRemaining());
        assertEquals(new BigDecimal("1300.00"), totals.actualRemaining());
        assertEquals(new BigDecimal("-130.00"), totals.closingDifference());
        assertEquals(new BigDecimal("100.00"), totals.subtotalB().difference());
    }

    private DreLineSnapshot snapshot(GroupKind kind, String name, String forecast, String paid) {
        UUID id = UUID.randomUUID();
        return new DreLineSnapshot(
                id,
                UUID.randomUUID(),
                "CAT",
                1,
                UUID.randomUUID(),
                kind,
                kind.name(),
                1,
                name,
                1,
                new BigDecimal(forecast),
                paid == null ? null : new BigDecimal(paid)
        );
    }
}
