package com.stric.recrutamento.dto.ia;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DadosExtraidosDTO {

    @JsonProperty("tempo_experiencia_anos")
    private Integer tempoExperienciaAnos;

    @JsonProperty("nivel_formacao")
    private String nivelFormacao;

    @JsonProperty("telefone_identificado")
    private String telefoneIdentificado;

    @JsonProperty("email_identificado")
    private String emailIdentificado;
}
