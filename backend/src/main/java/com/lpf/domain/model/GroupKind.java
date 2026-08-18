package com.lpf.domain.model;

public enum GroupKind {
    RECEITA,
    DESP_FIXA,
    DESP_VAR;

    public static GroupKind from(String raw) {
        for (GroupKind kind : values()) {
            if (kind.name().equals(raw)) {
                return kind;
            }
        }
        throw new IllegalArgumentException("Grupo financeiro desconhecido: " + raw);
    }
}
