package com.stric.recrutamento.dto.candidatura;

import com.stric.recrutamento.domain.entities.Candidatura;
import com.stric.recrutamento.domain.enums.StatusCandidatura;
import com.stric.recrutamento.dto.auth.UsuarioDTO;
import com.stric.recrutamento.dto.ia.AnaliseIAResponseDTO;
import com.stric.recrutamento.dto.vaga.VagaResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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
@NoArgsConstructor
@AllArgsConstructor
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
