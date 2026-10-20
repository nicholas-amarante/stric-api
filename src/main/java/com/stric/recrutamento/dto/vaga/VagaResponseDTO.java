package com.stric.recrutamento.dto.vaga;

import com.stric.recrutamento.domain.entities.Vaga;
import com.stric.recrutamento.domain.enums.StatusVaga;
import com.stric.recrutamento.dto.auth.UsuarioDTO;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class VagaResponseDTO {

    private UUID id;
    private String titulo;
    private String descricao;
    private List<String> requisitosObrigatorios;
    private List<String> requisitosDesejaveis;
    private Integer tempoMinimoExperienciaAnos;
    private StatusVaga status;
    private LocalDate dataLimiteInscricao;
    private UsuarioDTO recrutador;
    private LocalDateTime dataCriacao;
    private LocalDateTime dataAtualizacao;

    public VagaResponseDTO() {
    }

    @Builder
    public VagaResponseDTO(UUID id, String titulo, String descricao,
                           List<String> requisitosObrigatorios, List<String> requisitosDesejaveis,
                           Integer tempoMinimoExperienciaAnos, StatusVaga status, LocalDate dataLimiteInscricao,
                           UsuarioDTO recrutador, LocalDateTime dataCriacao,
                           LocalDateTime dataAtualizacao) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.requisitosObrigatorios = requisitosObrigatorios;
        this.requisitosDesejaveis = requisitosDesejaveis;
        this.tempoMinimoExperienciaAnos = tempoMinimoExperienciaAnos;
        this.status = status;
        this.dataLimiteInscricao = dataLimiteInscricao;
        this.recrutador = recrutador;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }

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
                .dataLimiteInscricao(vaga.getDataLimiteInscricao())
                .recrutador(UsuarioDTO.fromEntity(vaga.getRecrutador()))
                .dataCriacao(vaga.getDataCriacao())
                .dataAtualizacao(vaga.getDataAtualizacao())
                .build();
    }
}
