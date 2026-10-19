package com.stric.recrutamento.domain.entities;

import com.stric.recrutamento.util.StringListConverter;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "analises_ia", indexes = {
        @Index(name = "idx_analise_candidatura", columnList = "candidatura_id", unique = true)
})
@Getter
@Setter
public class AnaliseIA {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "candidatura_id", nullable = false, unique = true)
    private Candidatura candidatura;

    @Column(name = "score_aderencia", nullable = false)
    private Integer scoreAderencia;

    @Column(name = "resumo_executivo", nullable = false, columnDefinition = "TEXT")
    private String resumoExecutivo;

    @Convert(converter = StringListConverter.class)
    @Column(name = "requisitos_atendidos", columnDefinition = "TEXT")
    private List<String> requisitosAtendidos = new ArrayList<>();

    @Convert(converter = StringListConverter.class)
    @Column(name = "pontos_atencao", columnDefinition = "TEXT")
    private List<String> pontosAtencao = new ArrayList<>();

    @Column(name = "tempo_experiencia_anos")
    private Integer tempoExperienciaAnos;

    @Column(name = "nivel_formacao", length = 150)
    private String nivelFormacao;

    @Column(name = "telefone_identificado", length = 100)
    private String telefoneIdentificado;

    @Column(name = "email_identificado", length = 150)
    private String emailIdentificado;

    @Column(name = "raw_response_json", columnDefinition = "TEXT")
    private String rawResponseJson;

    @Column(name = "prompt_utilizado", columnDefinition = "TEXT")
    private String promptUtilizado;

    @Column(name = "mensagem_erro", columnDefinition = "TEXT")
    private String mensagemErro;

    @CreationTimestamp
    @Column(name = "data_processamento", nullable = false, updatable = false)
    private LocalDateTime dataProcessamento;

    public AnaliseIA() {
    }

    @Builder
    public AnaliseIA(UUID id, Candidatura candidatura, Integer scoreAderencia, String resumoExecutivo,
                     List<String> requisitosAtendidos, List<String> pontosAtencao,
                     Integer tempoExperienciaAnos, String nivelFormacao, String telefoneIdentificado,
                     String emailIdentificado, String rawResponseJson, String promptUtilizado,
                     String mensagemErro, LocalDateTime dataProcessamento) {
        this.id = id;
        this.candidatura = candidatura;
        this.scoreAderencia = scoreAderencia;
        this.resumoExecutivo = resumoExecutivo;
        this.requisitosAtendidos = requisitosAtendidos != null ? requisitosAtendidos : new ArrayList<>();
        this.pontosAtencao = pontosAtencao != null ? pontosAtencao : new ArrayList<>();
        this.tempoExperienciaAnos = tempoExperienciaAnos;
        this.nivelFormacao = nivelFormacao;
        this.telefoneIdentificado = telefoneIdentificado;
        this.emailIdentificado = emailIdentificado;
        this.rawResponseJson = rawResponseJson;
        this.promptUtilizado = promptUtilizado;
        this.mensagemErro = mensagemErro;
        this.dataProcessamento = dataProcessamento;
    }
}
