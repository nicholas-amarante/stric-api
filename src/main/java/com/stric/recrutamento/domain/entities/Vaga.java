package com.stric.recrutamento.domain.entities;

import com.stric.recrutamento.domain.enums.StatusVaga;
import com.stric.recrutamento.util.StringListConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "vagas", indexes = {
        @Index(name = "idx_vagas_status", columnList = "status"),
        @Index(name = "idx_vagas_recrutador", columnList = "recrutador_id")
})
@Getter
@Setter
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "titulo", nullable = false, length = 200)
    private String titulo;

    @Column(name = "descricao", nullable = false, columnDefinition = "TEXT")
    private String descricao;

    @Convert(converter = StringListConverter.class)
    @Column(name = "requisitos_obrigatorios", columnDefinition = "TEXT", nullable = false)
    private List<String> requisitosObrigatorios = new ArrayList<>();

    @Convert(converter = StringListConverter.class)
    @Column(name = "requisitos_desejaveis", columnDefinition = "TEXT")
    private List<String> requisitosDesejaveis = new ArrayList<>();

    @Column(name = "tempo_minimo_experiencia_anos", nullable = false)
    private Integer tempoMinimoExperienciaAnos = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private StatusVaga status = StatusVaga.ABERTA;

    @Column(name = "data_limite_inscricao")
    private LocalDate dataLimiteInscricao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recrutador_id", nullable = false)
    private Usuario recrutador;

    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    public Vaga() {
    }

    @Builder
    public Vaga(UUID id, String titulo, String descricao, List<String> requisitosObrigatorios,
                List<String> requisitosDesejaveis, Integer tempoMinimoExperienciaAnos,
                StatusVaga status, LocalDate dataLimiteInscricao, Usuario recrutador,
                LocalDateTime dataCriacao, LocalDateTime dataAtualizacao) {
        this.id = id;
        this.titulo = titulo;
        this.descricao = descricao;
        this.requisitosObrigatorios = requisitosObrigatorios != null ? requisitosObrigatorios : new ArrayList<>();
        this.requisitosDesejaveis = requisitosDesejaveis != null ? requisitosDesejaveis : new ArrayList<>();
        this.tempoMinimoExperienciaAnos = tempoMinimoExperienciaAnos != null ? tempoMinimoExperienciaAnos : 0;
        this.status = status != null ? status : StatusVaga.ABERTA;
        this.dataLimiteInscricao = dataLimiteInscricao;
        this.recrutador = recrutador;
        this.dataCriacao = dataCriacao;
        this.dataAtualizacao = dataAtualizacao;
    }
}
