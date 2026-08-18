package com.lpf.adapter.in.web.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record UpdateEntryRequest(BigDecimal forecast, BigDecimal paidAmount) {
}
