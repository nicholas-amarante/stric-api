package com.stric.recrutamento.dto.candidatura;

import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;

/**
 * Payload opcional para registrar justificativa na seleção para entrevista.
 */
@Data
public class SelecaoEntrevistaDTO {

    @Size(max = 500, message = "A justificativa deve ter no máximo 500 caracteres")
    private String justificativa;

    public SelecaoEntrevistaDTO() {
    }

    @Builder
    public SelecaoEntrevistaDTO(String justificativa) {
        this.justificativa = justificativa;
    }
}
