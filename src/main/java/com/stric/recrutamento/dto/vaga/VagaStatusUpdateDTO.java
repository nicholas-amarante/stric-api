package com.stric.recrutamento.dto.vaga;

import com.stric.recrutamento.domain.enums.StatusVaga;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
public class VagaStatusUpdateDTO {

    @NotNull(message = "O novo status da vaga é obrigatório (ABERTA, PAUSADA, ENCERRADA)")
    private StatusVaga status;

    public VagaStatusUpdateDTO() {
    }

    @Builder
    public VagaStatusUpdateDTO(StatusVaga status) {
        this.status = status;
    }
}
