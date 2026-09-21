package com.stric.recrutamento.dto.ia;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Retorno estruturado obrigatório da IA (Google Gemini).
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AnaliseIAResultDTO {

    @JsonProperty("score_aderencia")
    private Integer scoreAderencia;

    @JsonProperty("resumo_executivo")
    private String resumoExecutivo;

    @JsonProperty("requisitos_atendidos")
    @Builder.Default
    private List<String> requisitosAtendidos = new ArrayList<>();

    @JsonProperty("pontos_atencao")
    @Builder.Default
    private List<String> pontosAtencao = new ArrayList<>();

    @JsonProperty("dados_extraidos")
    private DadosExtraidosDTO dadosExtraidos;
}
