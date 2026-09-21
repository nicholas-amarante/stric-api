package com.stric.recrutamento.dto.vaga;

import com.stric.recrutamento.domain.entities.Vaga;
import com.stric.recrutamento.domain.enums.StatusVaga;
import com.stric.recrutamento.dto.auth.UsuarioDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VagaResponseDTO {

    private UUID id;
    private String titulo;
    private String descricao;
    private List<String> requisitosObrigatorios;
    private List<String> requisitosDesejaveis;
    private Integer tempoMinimoExperienciaAnos;
    private StatusVaga status;
    private UsuarioDTO recrutador;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public static VagaResponseDTO fromEntity(Vaga vaga) {
        if (vaga == null) return null;
        return VagaResponseDTO.builder()
                .id(vaga.getId())
                .titulo(vaga.getTitulo())
                .descricao(vaga.getDescricao())
                .requisitosObrigatorios(vaga.getRequisitosObrigatorios())
                .requisitosDesejaveis(vaga.getRequisitosDesejaveis())
                .tempoMinimoExperienciaAnos(vaga.getTempoMinimoExperienciaAnos())
                .status(vaga.getStatus())
                .recrutador(UsuarioDTO.fromEntity(vaga.getRecrutador()))
                .dataCriacao(vaga.getDataCriacao())
                .dataAtualizacao(vaga.getDataAtualizacao())
                .build();
    }
}
