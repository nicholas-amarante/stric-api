package com.stric.recrutamento.dto.candidatura;

import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Payload opcional para registrar justificativa na seleção para entrevista.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SelecaoEntrevistaDTO {

    @Size(max = 500, message = "A justificativa deve ter no máximo 500 caracteres")
    private String justificativa;
}
