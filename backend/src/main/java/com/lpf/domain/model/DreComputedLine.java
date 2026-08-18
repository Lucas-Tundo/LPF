package com.lpf.domain.model;

import java.math.BigDecimal;
import java.util.UUID;

public record DreComputedLine(
        UUID lineItemId,
        UUID categoryId,
        String categoryName,
        int categorySortOrder,
        UUID groupId,
        GroupKind groupKind,
        String groupName,
        int groupSortOrder,
        String name,
        int sortOrder,
        BigDecimal forecast,
        BigDecimal paidAmount,
        PaymentStatus status,
        BigDecimal difference
) {
}
