package com.lpf.domain.model;

import java.time.Instant;
import java.util.UUID;

public record Profile(
        UUID id,
        String email,
        String displayName,
        Instant onboardedAt
) {
}
