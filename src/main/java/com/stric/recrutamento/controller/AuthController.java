package com.stric.recrutamento.controller;

import com.stric.recrutamento.dto.auth.AuthResponse;
import com.stric.recrutamento.dto.auth.LoginRequest;
import com.stric.recrutamento.dto.auth.RegisterRequest;
import com.stric.recrutamento.dto.auth.UpdateProfileRequest;
import com.stric.recrutamento.dto.auth.UsuarioDTO;
import com.stric.recrutamento.dto.common.ApiResponse;
import com.stric.recrutamento.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@Tag(name = "1. Autenticação & Usuários", description = "Endpoints de cadastro, login e identificação com tokens JWT")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    @Operation(summary = "Registrar novo usuário", description = "Cria um novo usuário com perfil ROLE_RECRUITER ou ROLE_CANDIDATE e retorna o token JWT.")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.ok("Usuário cadastrado com sucesso.", response));
    }

    @PostMapping("/login")
    @Operation(summary = "Autenticar usuário", description = "Valida credenciais de e-mail e senha e retorna o token de autenticação JWT.")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.ok("Login realizado com sucesso.", response));
    }

    @GetMapping("/me")
    @Operation(summary = "Obter dados do usuário autenticado", description = "Retorna os dados cadastrais do usuário correspondente ao token enviado no cabeçalho Authorization.")
    public ResponseEntity<ApiResponse<UsuarioDTO>> getMe() {
        UsuarioDTO user = authService.getAuthenticatedUser();
        return ResponseEntity.ok(ApiResponse.ok(user));
    }

    @PutMapping("/profile")
    @Operation(summary = "Atualizar dados do usuário autenticado", description = "Atualiza nome, e-mail e senha do usuário logado no banco de dados.")
    public ResponseEntity<ApiResponse<AuthResponse>> updateProfile(@Valid @RequestBody UpdateProfileRequest request) {
        AuthResponse response = authService.updateProfile(request);
        return ResponseEntity.ok(ApiResponse.ok("Perfil atualizado com sucesso.", response));
    }
}
