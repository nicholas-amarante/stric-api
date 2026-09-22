package com.stric.recrutamento.dto.auth;

import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProfileRequest {
    private String nome;

    @Email(message = "E-mail em formato inválido.")
    private String email;

    private String senhaAtual;
    private String novaSenha;
}
