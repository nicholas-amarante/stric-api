package com.stric.recrutamento.dto.candidatura;

import com.stric.recrutamento.domain.enums.StatusCandidatura;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

/**
 * DTO com dados sensíveis mascarados em conformidade com a LGPD
 * para listagem geral do recrutador antes da liberação de entrevista.
 */
@Data
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

    public CandidaturaMaskedDTO() {
    }

    @Builder
    public CandidaturaMaskedDTO(UUID id, UUID vagaId, String tituloVaga, UUID candidatoId,
                                String nomeCandidatoMascarado, String emailCandidatoMascarado,
                                String telefoneMascarado, boolean dadosContatoLiberados,
                                StatusCandidatura status, Integer scoreAderencia,
                                String resumoExecutivo, List<String> requisitosAtendidos,
                                List<String> pontosAtencao, Integer tempoExperienciaAnos,
                                String nivelFormacao, String nomeOriginalArquivo,
                                Long tamanhoArquivoBytes, LocalDateTime dataSubmissao) {
        this.id = id;
        this.vagaId = vagaId;
        this.tituloVaga = tituloVaga;
        this.candidatoId = candidatoId;
        this.nomeCandidatoMascarado = nomeCandidatoMascarado;
        this.emailCandidatoMascarado = emailCandidatoMascarado;
        this.telefoneMascarado = telefoneMascarado;
        this.dadosContatoLiberados = dadosContatoLiberados;
        this.status = status;
        this.scoreAderencia = scoreAderencia;
        this.resumoExecutivo = resumoExecutivo;
        this.requisitosAtendidos = requisitosAtendidos;
        this.pontosAtencao = pontosAtencao;
        this.tempoExperienciaAnos = tempoExperienciaAnos;
        this.nivelFormacao = nivelFormacao;
        this.nomeOriginalArquivo = nomeOriginalArquivo;
        this.tamanhoArquivoBytes = tamanhoArquivoBytes;
        this.dataSubmissao = dataSubmissao;
    }
}
