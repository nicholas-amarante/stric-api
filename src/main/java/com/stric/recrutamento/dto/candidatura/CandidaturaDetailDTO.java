package com.stric.recrutamento.dto.candidatura;

import com.stric.recrutamento.domain.entities.Candidatura;
import com.stric.recrutamento.domain.enums.StatusCandidatura;
import com.stric.recrutamento.dto.auth.UsuarioDTO;
import com.stric.recrutamento.dto.ia.AnaliseIAResponseDTO;
import com.stric.recrutamento.dto.vaga.VagaResponseDTO;
import lombok.Builder;
import lombok.Data;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * DTO com dados completos da candidatura e análise de IA.
 * Utilizado pelo próprio candidato ou pelo recrutador quando o status é SELECIONADO_ENTREVISTA.
 */
@Data
@Builder
public class CandidaturaDetailDTO {

    private UUID id;
    private VagaResponseDTO vaga;
    private UsuarioDTO candidato;
    private StatusCandidatura status;
    private String nomeOriginalArquivo;
    private Long tamanhoArquivoBytes;
    private LocalDateTime dataSubmissao;
    private LocalDateTime dataAtualizacao;
    private AnaliseIAResponseDTO analiseIA;
    private List<AuditoriaResponseDTO> auditorias;

    public CandidaturaDetailDTO() {
    }

    public CandidaturaDetailDTO(UUID id, VagaResponseDTO vaga, UsuarioDTO candidato,
                                StatusCandidatura status, String nomeOriginalArquivo,
                                Long tamanhoArquivoBytes, LocalDateTime dataSubmissao,
                                LocalDateTime dataAtualizacao, AnaliseIAResponseDTO analiseIA,
                                List<AuditoriaResponseDTO> auditorias) {
        this.id = id;
        this.vaga = vaga;
        this.candidato = candidato;
        this.status = status;
        this.nomeOriginalArquivo = nomeOriginalArquivo;
        this.tamanhoArquivoBytes = tamanhoArquivoBytes;
        this.dataSubmissao = dataSubmissao;
        this.dataAtualizacao = dataAtualizacao;
        this.analiseIA = analiseIA;
        this.auditorias = auditorias;
    }

    @JsonProperty("vagaId")
    public UUID getVagaId() {
        return vaga != null ? vaga.getId() : null;
    }

    @JsonProperty("vagaTitulo")
    public String getVagaTitulo() {
        return vaga != null ? vaga.getTitulo() : null;
    }

    @JsonProperty("candidatoNome")
    public String getCandidatoNome() {
        return candidato != null ? candidato.getNome() : null;
    }

    public static CandidaturaDetailDTO fromEntity(Candidatura candidatura) {
        if (candidatura == null) return null;
        return CandidaturaDetailDTO.builder()
                .id(candidatura.getId())
                .vaga(VagaResponseDTO.fromEntity(candidatura.getVaga()))
                .candidato(UsuarioDTO.fromEntity(candidatura.getCandidato()))
                .status(candidatura.getStatus())
                .nomeOriginalArquivo(candidatura.getNomeOriginalArquivo())
                .tamanhoArquivoBytes(candidatura.getTamanhoArquivoBytes())
                .dataSubmissao(candidatura.getDataSubmissao())
                .dataAtualizacao(candidatura.getDataAtualizacao())
                .analiseIA(AnaliseIAResponseDTO.fromEntity(candidatura.getAnaliseIA()))
                .auditorias(candidatura.getAuditorias() != null ?
                        candidatura.getAuditorias().stream()
                                .map(AuditoriaResponseDTO::fromEntity)
                                .collect(Collectors.toList()) : List.of())
                .build();
    }
}
