package com.stric.recrutamento.dto.candidatura;

import com.stric.recrutamento.domain.enums.StatusCandidatura;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Resposta imediata HTTP 202 (Accepted) do upload de currículo (RN03).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UploadResponseDTO {

    @JsonProperty("id")
    private UUID candidaturaId;
    private UUID vagaId;
    private String tituloVaga;
    private StatusCandidatura status;
    private String mensagem;
    private LocalDateTime dataSubmissao;
}
