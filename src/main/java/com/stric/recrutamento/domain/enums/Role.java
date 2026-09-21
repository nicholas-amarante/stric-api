package com.stric.recrutamento.domain.enums;

/**
 * Perfis de acesso ao sistema (RBAC).
 */
public enum Role {
    ROLE_RECRUITER,
    ROLE_CANDIDATE;

    public String getSimpleName() {
        return this.name().replace("ROLE_", "");
    }
}
