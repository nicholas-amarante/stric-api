package com.stric.recrutamento.config;

import com.stric.recrutamento.domain.entities.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SecurityException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

/**
 * Provedor de operações com tokens JWT utilizando a biblioteca JJWT 0.12.x.
 */
@Slf4j
@Component
public class JwtTokenProvider {

    @Value("${jwt.secret:404E635266556A586E3272357538782F413F4428472B4B6250645367566B5970}")
    private String jwtSecret;

    @Value("${jwt.expiration:86400000}")
    private long jwtExpirationInMs;

    private SecretKey secretKey;

    @PostConstruct
    public void init() {
        // Garante que a chave possua tamanho adequado para HMAC-SHA256
        byte[] keyBytes = jwtSecret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            String padded = String.format("%-32s", jwtSecret).replace(' ', '0');
            keyBytes = padded.getBytes(StandardCharsets.UTF_8);
        }
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

    /**
     * Gera um novo token JWT para o usuário autenticado.
     */
    public String generateToken(Usuario usuario) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationInMs);

        return Jwts.builder()
                .subject(usuario.getEmail())
                .claim("id", usuario.getId().toString())
                .claim("nome", usuario.getNome())
                .claim("role", usuario.getRole().name())
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(secretKey)
                .compact();
    }

    /**
     * Extrai o e-mail (subject) do token JWT.
     */
    public String getEmailFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.getSubject();
    }

    /**
     * Extrai o ID do usuário do token JWT.
     */
    public UUID getUserIdFromToken(String token) {
        Claims claims = getClaims(token);
        String idStr = claims.get("id", String.class);
        return UUID.fromString(idStr);
    }

    /**
     * Extrai o perfil (Role) do token JWT.
     */
    public String getRoleFromToken(String token) {
        Claims claims = getClaims(token);
        return claims.get("role", String.class);
    }

    /**
     * Retorna os Claims contidos no token.
     */
    public Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    /**
     * Valida a integridade e expiração do token JWT.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException ex) {
            log.warn("Assinatura JWT inválida ou malformada: {}", ex.getMessage());
        } catch (ExpiredJwtException ex) {
            log.warn("Token JWT expirado: {}", ex.getMessage());
        } catch (UnsupportedJwtException ex) {
            log.warn("Token JWT não suportado: {}", ex.getMessage());
        } catch (IllegalArgumentException ex) {
            log.warn("String de claims JWT está vazia: {}", ex.getMessage());
        }
        return false;
    }

    public long getExpirationInMs() {
        return jwtExpirationInMs;
    }
}
