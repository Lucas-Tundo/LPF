package com.lpf.domain.model;

import java.util.List;

public record DreMonth(
        int year,
        int month,
        List<DreComputedLine> lines,
        DreTotals totals
) {
}
