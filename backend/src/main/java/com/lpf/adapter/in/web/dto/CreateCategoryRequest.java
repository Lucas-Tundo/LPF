package com.lpf.adapter.in.web.dto;

import java.util.UUID;

public record CreateCategoryRequest(UUID groupId, String name) {
}
