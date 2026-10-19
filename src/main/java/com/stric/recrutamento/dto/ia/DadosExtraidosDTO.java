package com.stric.recrutamento.dto.ia;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Data;

@Data
public class DadosExtraidosDTO {

    @JsonProperty("tempo_experiencia_anos")
    private Integer tempoExperienciaAnos;

    @JsonProperty("nivel_formacao")
    private String nivelFormacao;

    @JsonProperty("telefone_identificado")
    private String telefoneIdentificado;

    @JsonProperty("email_identificado")
    private String emailIdentificado;

    public DadosExtraidosDTO() {
    }

    @Builder
    public DadosExtraidosDTO(Integer tempoExperienciaAnos, String nivelFormacao,
                            String telefoneIdentificado, String emailIdentificado) {
        this.tempoExperienciaAnos = tempoExperienciaAnos;
        this.nivelFormacao = nivelFormacao;
        this.telefoneIdentificado = telefoneIdentificado;
        this.emailIdentificado = emailIdentificado;
    }
}
