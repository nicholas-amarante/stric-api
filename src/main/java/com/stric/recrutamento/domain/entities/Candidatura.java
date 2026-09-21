package com.stric.recrutamento.domain.entities;

import com.stric.recrutamento.domain.enums.StatusCandidatura;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "candidaturas", uniqueConstraints = {
        @UniqueConstraint(name = "uk_candidatura_vaga_candidato", columnNames = {"vaga_id", "candidato_id"})
}, indexes = {
        @Index(name = "idx_candidatura_vaga", columnList = "vaga_id"),
        @Index(name = "idx_candidatura_candidato", columnList = "candidato_id"),
        @Index(name = "idx_candidatura_status", columnList = "status")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Candidatura {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vaga_id", nullable = false)
    private Vaga vaga;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidato_id", nullable = false)
    private Usuario candidato;

    @Column(name = "caminho_curriculo_pdf", nullable = false, length = 500)
    private String caminhoCurriculoPdf;

    @Column(name = "nome_original_arquivo", nullable = false, length = 255)
    private String nomeOriginalArquivo;

    @Column(name = "tamanho_arquivo_bytes", nullable = false)
    private Long tamanhoArquivoBytes;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 40)
    @Builder.Default
    private StatusCandidatura status = StatusCandidatura.EM_ANALISE;

    @CreationTimestamp
    @Column(name = "data_submissao", nullable = false, updatable = false)
    private LocalDateTime dataSubmissao;

    @UpdateTimestamp
    @Column(name = "data_atualizacao")
    private LocalDateTime dataAtualizacao;

    @OneToOne(mappedBy = "candidatura", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private AnaliseIA analiseIA;

    @OneToMany(mappedBy = "candidatura", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default
    private List<AuditoriaAcessoContato> auditorias = new ArrayList<>();
}
