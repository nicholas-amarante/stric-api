package com.stric.recrutamento.domain.entities;

import com.stric.recrutamento.domain.enums.StatusVaga;
import com.stric.recrutamento.util.StringListConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

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
@NoArgsConstructor
@AllArgsConstructor
@Builder
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
    @Builder.Default
    private List<String> requisitosObrigatorios = new ArrayList<>();

    @Convert(converter = StringListConverter.class)
    @Column(name = "requisitos_desejaveis", columnDefinition = "TEXT")
    @Builder.Default
    private List<String> requisitosDesejaveis = new ArrayList<>();

    @Column(name = "tempo_minimo_experiencia_anos", nullable = false)
    @Builder.Default
    private Integer tempoMinimoExperienciaAnos = 0;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    @Builder.Default
    private StatusVaga status = StatusVaga.ABERTA;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recrutador_id", nullable = false)
    private Usuario recrutador;

    @CreationTimestamp
    @Column(name = "data_criacao", nullable = false, updatable = false)
    private LocalDateTime dataCriacao;

    @UpdateTimestamp
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;
}
