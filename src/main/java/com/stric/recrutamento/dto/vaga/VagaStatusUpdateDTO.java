package com.stric.recrutamento.dto.vaga;

import com.stric.recrutamento.domain.enums.StatusVaga;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VagaStatusUpdateDTO {

    @NotNull(message = "O novo status da vaga é obrigatório (ABERTA, PAUSADA, ENCERRADA)")
    private StatusVaga status;
}
