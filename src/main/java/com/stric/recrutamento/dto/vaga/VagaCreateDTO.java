package com.stric.recrutamento.dto.vaga;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO para cadastro de novas vagas de emprego (RN01).
 */
@Data
public class VagaCreateDTO {

    @NotBlank(message = "O título da vaga é obrigatório")
    @Size(min = 3, max = 200, message = "O título deve ter entre 3 e 200 caracteres")
    private String titulo;

    @NotBlank(message = "A descrição detalhada da vaga é obrigatória")
    private String descricao;

    @NotEmpty(message = "Informe ao menos um requisito obrigatório")
    private List<String> requisitosObrigatorios = new ArrayList<>();

    private List<String> requisitosDesejaveis = new ArrayList<>();

    @Min(value = 0, message = "O tempo mínimo de experiência em anos não pode ser negativo")
    private Integer tempoMinimoExperienciaAnos = 0;

    private LocalDate dataLimiteInscricao;

    public VagaCreateDTO() {
    }

    @Builder
    public VagaCreateDTO(String titulo, String descricao, List<String> requisitosObrigatorios,
                         List<String> requisitosDesejaveis, Integer tempoMinimoExperienciaAnos,
                         LocalDate dataLimiteInscricao) {
        this.titulo = titulo;
        this.descricao = descricao;
        this.requisitosObrigatorios = requisitosObrigatorios != null ? requisitosObrigatorios : new ArrayList<>();
        this.requisitosDesejaveis = requisitosDesejaveis != null ? requisitosDesejaveis : new ArrayList<>();
        this.tempoMinimoExperienciaAnos = tempoMinimoExperienciaAnos != null ? tempoMinimoExperienciaAnos : 0;
        this.dataLimiteInscricao = dataLimiteInscricao;
    }
}
