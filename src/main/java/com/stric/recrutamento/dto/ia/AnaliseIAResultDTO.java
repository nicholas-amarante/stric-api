package com.stric.recrutamento.dto.ia;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Retorno estruturado obrigatório da IA (Google Gemini).
 */
@Data
public class AnaliseIAResultDTO {

    @JsonProperty("score_aderencia")
    private Integer scoreAderencia;

    @JsonProperty("resumo_executivo")
    private String resumoExecutivo;

    @JsonProperty("requisitos_atendidos")
    private List<String> requisitosAtendidos = new ArrayList<>();

    @JsonProperty("pontos_atencao")
    private List<String> pontosAtencao = new ArrayList<>();

    @JsonProperty("dados_extraidos")
    private DadosExtraidosDTO dadosExtraidos;

    public AnaliseIAResultDTO() {
    }

    @Builder
    public AnaliseIAResultDTO(Integer scoreAderencia, String resumoExecutivo,
                              List<String> requisitosAtendidos, List<String> pontosAtencao,
                              DadosExtraidosDTO dadosExtraidos) {
        this.scoreAderencia = scoreAderencia;
        this.resumoExecutivo = resumoExecutivo;
        this.requisitosAtendidos = requisitosAtendidos != null ? requisitosAtendidos : new ArrayList<>();
        this.pontosAtencao = pontosAtencao != null ? pontosAtencao : new ArrayList<>();
        this.dadosExtraidos = dadosExtraidos;
    }
}
