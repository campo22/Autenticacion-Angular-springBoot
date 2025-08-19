package com.diver.autenticacion.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;
import java.util.function.Function;

@Slf4j
@Component
public class JwtUtils {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Integer expiration;

    @Value("${jwt.refreshExpiration}")
    private Integer refreshExpiration;

    private Key key;

    @PostConstruct
    public void init() {
        // Asegúrate de que tu secreto sea lo suficientemente largo para HS256 (mínimo 32 bytes/256 bits)
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < 32) {
            log.warn("⚠️ La clave secreta JWT es menor de 256 bits, lo cual no es seguro para producción.");
        }
        this.key = Keys.hmacShaKeyFor(keyBytes);
        log.info("✅ JWT Utils inicializado correctamente.");
    }

    /**
     * Método centralizado para construir cualquier tipo de token.
     * @param username El sujeto del token.
     * @param tokenExpiration Duración del token en milisegundos.
     * @return El token JWT compacto como String.
     */
    private String buildToken(String username, Integer tokenExpiration) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
                .signWith(this.key, SignatureAlgorithm.HS256)
                .compact();
    }

    // --- MÉTODOS PÚBLICOS DE GENERACIÓN ---

    public String generateToken(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        log.info("🔑 Generando el Access Token para usuario: {}", username);
        return buildToken(username, this.expiration);
    }

    public String generateTokenFromUsername(String username) {
        log.info("🔑 Generando Access Token para usuario: {}", username);
        return buildToken(username, this.expiration);
    }

    public String generateRefreshToken(Authentication authentication) {
        String username = ((UserDetails) authentication.getPrincipal()).getUsername();
        log.info("♻️ Generando el Refresh Token para usuario: {}", username);
        return buildToken(username, this.refreshExpiration);
    }

    public String generateRefreshTokenFromUsername(String username) {
        log.info("♻️ Generando Refresh Token para usuario: {}", username);
        return buildToken(username, this.refreshExpiration);
    }

    // --- MÉTODOS PÚBLICOS DE VALIDACIÓN Y EXTRACCIÓN ---

    /**
     * Valida un token JWT. Comprueba la firma, la expiración y la estructura.
     * @param token El token a validar.
     * @return true si el token es completamente válido, false en caso contrario.
     */
    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(this.key)
                    .build()
                    .parseClaimsJws(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.warn("⚠️ JWT token ha expirado: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("❌ JWT token no es soportado: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            log.error("❌ JWT token malformado: {}", e.getMessage());
        } catch (SignatureException e) {
            log.error("❌ Firma de JWT inválida: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("❌ JWT claims están vacíos o son ilegales: {}", e.getMessage());
        }
        return false;
    }

    /**
     * Extrae el nombre de usuario (subject) de un token JWT.
     * Este método fallará si el token no es válido (expirado, firma incorrecta, etc.).
     * @param token El token del cual extraer el username.
     * @return El nombre de usuario.
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        // Este es el método que realmente realiza la validación completa.
        // Si algo está mal, lanzará una de las excepciones JwtException.
        return Jwts.parserBuilder()
                .setSigningKey(this.key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}