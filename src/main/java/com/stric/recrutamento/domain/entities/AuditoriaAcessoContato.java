package com.stric.recrutamento.domain.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Registro de auditoria em conformidade com a LGPD quando dados de contato
 * são liberados/acessados pelo recrutador.
 */
@Entity
@Table(name = "auditorias_acesso_contato", indexes = {
        @Index(name = "idx_auditoria_candidatura", columnList = "candidatura_id"),
        @Index(name = "idx_auditoria_recrutador", columnList = "recrutador_id")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditoriaAcessoContato {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidatura_id", nullable = false)
    private Candidatura candidatura;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "recrutador_id", nullable = false)
    private Usuario recrutador;

    @Column(name = "acao", nullable = false, length = 100)
    private String acao;

    @Column(name = "justificativa", columnDefinition = "TEXT")
    private String justificativa;

    @CreationTimestamp
    @Column(name = "data_acesso", nullable = false, updatable = false)
    private LocalDateTime dataAcesso;
}
