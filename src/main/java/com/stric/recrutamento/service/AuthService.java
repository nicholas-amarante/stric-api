package com.stric.recrutamento.service;

import com.stric.recrutamento.config.JwtTokenProvider;
import com.stric.recrutamento.domain.entities.Usuario;
import com.stric.recrutamento.domain.repositories.UsuarioRepository;
import com.stric.recrutamento.dto.auth.AuthResponse;
import com.stric.recrutamento.dto.auth.LoginRequest;
import com.stric.recrutamento.dto.auth.RegisterRequest;
import com.stric.recrutamento.dto.auth.UpdateProfileRequest;
import com.stric.recrutamento.dto.auth.UsuarioDTO;
import com.stric.recrutamento.exception.BusinessException;
import com.stric.recrutamento.util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final SecurityUtils securityUtils;

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (usuarioRepository.existsByEmail(request.getEmail().toLowerCase().trim())) {
            throw new BusinessException("Já existe um usuário cadastrado com este e-mail.");
        }

        Usuario usuario = Usuario.builder()
                .nome(request.getNome().trim())
                .email(request.getEmail().toLowerCase().trim())
                .senha(passwordEncoder.encode(request.getSenha()))
                .role(request.getRole())
                .build();

        usuario = usuarioRepository.save(usuario);
        log.info("Novo usuário registrado com sucesso: ID={}, Email={}, Role={}",
                usuario.getId(), usuario.getEmail(), usuario.getRole());

        String token = jwtTokenProvider.generateToken(usuario);

        return AuthResponse.builder()
                .token(token)
                .expiraEmMilissegundos(jwtTokenProvider.getExpirationInMs())
                .usuario(UsuarioDTO.fromEntity(usuario))
                .build();
    }

    @Transactional(readOnly = true)
    public AuthResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmail(request.getEmail().toLowerCase().trim())
                .orElseThrow(() -> new BadCredentialsException("Credenciais inválidas."));

        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            throw new BadCredentialsException("Credenciais inválidas.");
        }

        log.info("Usuário autenticado com sucesso: Email={}", usuario.getEmail());
        String token = jwtTokenProvider.generateToken(usuario);

        return AuthResponse.builder()
                .token(token)
                .expiraEmMilissegundos(jwtTokenProvider.getExpirationInMs())
                .usuario(UsuarioDTO.fromEntity(usuario))
                .build();
    }

    @Transactional(readOnly = true)
    public UsuarioDTO getAuthenticatedUser() {
        Usuario usuario = securityUtils.getCurrentUser();
        return UsuarioDTO.fromEntity(usuario);
    }

    @Transactional
    public AuthResponse updateProfile(UpdateProfileRequest request) {
        Usuario usuario = securityUtils.getCurrentUser();

        if (request.getSenhaAtual() != null && !request.getSenhaAtual().isBlank()) {
            if (!passwordEncoder.matches(request.getSenhaAtual(), usuario.getSenha())) {
                throw new BadCredentialsException("A senha atual informada está incorreta.");
            }
        }

        if (request.getNome() != null && !request.getNome().isBlank()) {
            usuario.setNome(request.getNome().trim());
        }

        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            String novoEmail = request.getEmail().toLowerCase().trim();
            if (!novoEmail.equalsIgnoreCase(usuario.getEmail())) {
                if (request.getSenhaAtual() == null || request.getSenhaAtual().isBlank()) {
                    throw new BadCredentialsException("Para alterar o e-mail, informe a sua senha atual.");
                }
                if (usuarioRepository.existsByEmail(novoEmail)) {
                    throw new BusinessException("Já existe um usuário cadastrado com este e-mail.");
                }
                usuario.setEmail(novoEmail);
            }
        }

        if (request.getNovaSenha() != null && !request.getNovaSenha().isBlank()) {
            if (request.getSenhaAtual() == null || request.getSenhaAtual().isBlank()) {
                throw new BadCredentialsException("Para alterar a senha, informe a sua senha atual.");
            }
            usuario.setSenha(passwordEncoder.encode(request.getNovaSenha()));
        }

        usuario = usuarioRepository.save(usuario);
        log.info("Perfil do usuário atualizado com sucesso no banco de dados: ID={}, Email={}", usuario.getId(), usuario.getEmail());

        String token = jwtTokenProvider.generateToken(usuario);

        return AuthResponse.builder()
                .token(token)
                .expiraEmMilissegundos(jwtTokenProvider.getExpirationInMs())
                .usuario(UsuarioDTO.fromEntity(usuario))
                .build();
    }
}
