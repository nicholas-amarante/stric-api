package com.stric.recrutamento.dto.auth;

import jakarta.validation.constraints.Email;
import lombok.Builder;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    private String nome;

    @Email(message = "E-mail em formato inválido.")
    private String email;

    private String senhaAtual;
    private String novaSenha;

    public UpdateProfileRequest() {
    }

    @Builder
    public UpdateProfileRequest(String nome, String email, String senhaAtual, String novaSenha) {
        this.nome = nome;
        this.email = email;
        this.senhaAtual = senhaAtual;
        this.novaSenha = novaSenha;
    }
}
