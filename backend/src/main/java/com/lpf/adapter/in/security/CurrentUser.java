package com.lpf.adapter.in.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.Map;
import java.util.UUID;

public final class CurrentUser {

    private CurrentUser() {
    }

    public static UUID id() {
        return UUID.fromString(jwt().getSubject());
    }

    public static String email() {
        String email = jwt().getClaimAsString("email");
        return email == null ? "" : email;
    }

    public static String displayName() {
        Map<String, Object> metadata = jwt().getClaim("user_metadata");
        if (metadata != null && metadata.get("full_name") instanceof String fullName && !fullName.isBlank()) {
            return fullName;
        }
        String name = jwt().getClaimAsString("name");
        return name == null || name.isBlank() ? email() : name;
    }

    private static Jwt jwt() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof Jwt jwt)) {
            throw new IllegalStateException("Usuário autenticado não encontrado");
        }
        return jwt;
    }
}
