package com.stric.recrutamento.dto.auth;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

@Data
public class UpdateProfileRequest {
    @Size(max = 150, message = "O nome deve ter no máximo 150 caracteres.")
    private String nome;

    @Email(message = "E-mail em formato inválido.")
    @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres.")
    private String email;

    private String senhaAtual;

    @Size(min = 6, max = 100, message = "A nova senha deve ter entre 6 e 100 caracteres.")
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
