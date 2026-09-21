package com.stric.recrutamento.dto.candidatura;

import com.stric.recrutamento.domain.entities.AuditoriaAcessoContato;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuditoriaResponseDTO {

    private UUID id;
    private String nomeRecrutador;
    private String emailRecrutador;
    private String acao;
    private String justificativa;
    private LocalDateTime dataAcesso;

    public static AuditoriaResponseDTO fromEntity(AuditoriaAcessoContato auditoria) {
        if (auditoria == null) return null;
        return AuditoriaResponseDTO.builder()
                .id(auditoria.getId())
                .nomeRecrutador(auditoria.getRecrutador() != null ? auditoria.getRecrutador().getNome() : null)
                .emailRecrutador(auditoria.getRecrutador() != null ? auditoria.getRecrutador().getEmail() : null)
                .acao(auditoria.getAcao())
                .justificativa(auditoria.getJustificativa())
                .dataAcesso(auditoria.getDataAcesso())
                .build();
    }
}
