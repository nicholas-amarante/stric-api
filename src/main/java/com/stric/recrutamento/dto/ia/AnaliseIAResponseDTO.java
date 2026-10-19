package com.stric.recrutamento.dto.ia;

import com.stric.recrutamento.domain.entities.AnaliseIA;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
public class AnaliseIAResponseDTO {

    private UUID id;
    private Integer scoreAderencia;
    private String resumoExecutivo;
    private List<String> requisitosAtendidos;
    private List<String> pontosAtencao;
    private Integer tempoExperienciaAnos;
    private String nivelFormacao;
    private String telefoneIdentificado;
    private String emailIdentificado;
    private String mensagemErro;
    private LocalDateTime dataProcessamento;

    public AnaliseIAResponseDTO() {
    }

    @Builder
    public AnaliseIAResponseDTO(UUID id, Integer scoreAderencia, String resumoExecutivo,
                                List<String> requisitosAtendidos, List<String> pontosAtencao,
                                Integer tempoExperienciaAnos, String nivelFormacao,
                                String telefoneIdentificado, String emailIdentificado,
                                String mensagemErro, LocalDateTime dataProcessamento) {
        this.id = id;
        this.scoreAderencia = scoreAderencia;
        this.resumoExecutivo = resumoExecutivo;
        this.requisitosAtendidos = requisitosAtendidos;
        this.pontosAtencao = pontosAtencao;
        this.tempoExperienciaAnos = tempoExperienciaAnos;
        this.nivelFormacao = nivelFormacao;
        this.telefoneIdentificado = telefoneIdentificado;
        this.emailIdentificado = emailIdentificado;
        this.mensagemErro = mensagemErro;
        this.dataProcessamento = dataProcessamento;
    }

    public static AnaliseIAResponseDTO fromEntity(AnaliseIA analiseIA) {
        if (analiseIA == null) return null;
        return AnaliseIAResponseDTO.builder()
                .id(analiseIA.getId())
                .scoreAderencia(analiseIA.getScoreAderencia())
                .resumoExecutivo(analiseIA.getResumoExecutivo())
                .requisitosAtendidos(analiseIA.getRequisitosAtendidos())
                .pontosAtencao(analiseIA.getPontosAtencao())
                .tempoExperienciaAnos(analiseIA.getTempoExperienciaAnos())
                .nivelFormacao(analiseIA.getNivelFormacao())
                .telefoneIdentificado(analiseIA.getTelefoneIdentificado())
                .emailIdentificado(analiseIA.getEmailIdentificado())
                .mensagemErro(analiseIA.getMensagemErro())
                .dataProcessamento(analiseIA.getDataProcessamento())
                .build();
    }
}
