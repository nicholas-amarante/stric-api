package com.stric.recrutamento.dto.auth;

import com.stric.recrutamento.domain.entities.Usuario;
import com.stric.recrutamento.domain.enums.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class UsuarioDTO {

    private UUID id;
    private String nome;
    private String email;
    private Role role;
    private LocalDateTime dataCriacao;

    public UsuarioDTO() {
    }

    @Builder
    public UsuarioDTO(UUID id, String nome, String email, Role role, LocalDateTime dataCriacao) {
        this.id = id;
        this.nome = nome;
        this.email = email;
        this.role = role;
        this.dataCriacao = dataCriacao;
    }

    public static UsuarioDTO fromEntity(Usuario usuario) {
        if (usuario == null) return null;
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nome(usuario.getNome())
                .email(usuario.getEmail())
                .role(usuario.getRole())
                .dataCriacao(usuario.getDataCriacao())
                .build();
    }
}
