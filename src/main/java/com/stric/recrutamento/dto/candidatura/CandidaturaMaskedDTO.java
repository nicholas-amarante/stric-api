package com.stric.recrutamento.dto.candidatura;

import com.stric.recrutamento.domain.enums.StatusCandidatura;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO com dados sensíveis mascarados em conformidade com a LGPD
 * para listagem geral do recrutador antes da liberação de entrevista.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidaturaMaskedDTO {

    private UUID id;
    private UUID vagaId;
    private String tituloVaga;
    private UUID candidatoId;
    
    // Dados mascarados ou ocultados
    private String nomeCandidatoMascarado;
    private String emailCandidatoMascarado;
    private String telefoneMascarado;
    private boolean dadosContatoLiberados;

    private StatusCandidatura status;
    private Integer scoreAderencia;
    private String resumoExecutivo;
    private List<String> requisitosAtendidos;
    private List<String> pontosAtencao;
    private Integer tempoExperienciaAnos;
    private String nivelFormacao;
    private String nomeOriginalArquivo;
    private Long tamanhoArquivoBytes;
    private LocalDateTime dataSubmissao;
}
