package com.stric.recrutamento.dto.auth;

import lombok.Builder;
import lombok.Data;

@Data
public class AuthResponse {

    private String token;
    private String tipo = "Bearer";
    private long expiraEmMilissegundos;
    private UsuarioDTO usuario;

    public AuthResponse() {
    }

    @Builder
    public AuthResponse(String token, String tipo, long expiraEmMilissegundos, UsuarioDTO usuario) {
        this.token = token;
        this.tipo = tipo != null ? tipo : "Bearer";
        this.expiraEmMilissegundos = expiraEmMilissegundos;
        this.usuario = usuario;
    }
}
