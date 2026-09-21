package com.stric.recrutamento.util;

import com.stric.recrutamento.domain.entities.Usuario;
import com.stric.recrutamento.domain.enums.Role;
import com.stric.recrutamento.domain.repositories.UsuarioRepository;
import com.stric.recrutamento.exception.ResourceNotFoundException;
import com.stric.recrutamento.exception.UnauthorizedAccessException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UsuarioRepository usuarioRepository;

    public String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated() || "anonymousUser".equals(authentication.getPrincipal())) {
            throw new UnauthorizedAccessException("Usuário não autenticado no contexto de segurança.");
        }
        return authentication.getName();
    }

    public Usuario getCurrentUser() {
        String email = getCurrentUserEmail();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário autenticado não encontrado no banco de dados."));
    }

    public boolean hasRole(Role role) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) return false;
        return authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(role.name()));
    }
}
