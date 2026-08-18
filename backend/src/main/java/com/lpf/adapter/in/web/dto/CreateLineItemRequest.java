package com.lpf.adapter.in.web.dto;

import java.util.UUID;

public record CreateLineItemRequest(UUID categoryId, String name) {
}
