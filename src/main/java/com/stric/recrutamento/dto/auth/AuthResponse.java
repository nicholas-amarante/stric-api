package com.stric.recrutamento.dto.auth;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {

    private String token;
    @Builder.Default
    private String tipo = "Bearer";
    private long expiraEmMilissegundos;
    private UsuarioDTO usuario;
}
