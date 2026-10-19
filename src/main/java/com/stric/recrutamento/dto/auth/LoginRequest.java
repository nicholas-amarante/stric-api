package com.stric.recrutamento.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
public class LoginRequest {

    @NotBlank(message = "O e-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @NotBlank(message = "A senha é obrigatória")
    private String senha;

    public LoginRequest() {
    }

    @Builder
    public LoginRequest(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }
}
