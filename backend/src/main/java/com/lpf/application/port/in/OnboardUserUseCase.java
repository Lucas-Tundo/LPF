package com.lpf.application.port.in;

import java.util.UUID;

public interface OnboardUserUseCase {
    void ensureReady(UUID userId, String email, String displayName);
}
