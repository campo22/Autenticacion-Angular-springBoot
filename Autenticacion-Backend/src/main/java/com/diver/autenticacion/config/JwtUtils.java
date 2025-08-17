package com.diver.autenticacion.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
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
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        this.key = Keys.hmacShaKeyFor(keyBytes);
        log.info("✅ JWT Utils inicializado con clave secreta de longitud: {}", keyBytes.length);
    }

    // 🎟️ Generar Access Token
    public String generateToken(Authentication authentication) {
        UserDetails mainUser = (UserDetails) authentication.getPrincipal();
        String username = mainUser.getUsername();

        String token = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(this.key, SignatureAlgorithm.HS256)
                .compact();

        log.info("🔑 Access Token generado para usuario: {} con expiración en {} ms", username, expiration);
        return token;
    }

    // 🔄 Generar Refresh Token
    public String generateRefreshToken(Authentication authentication) {
        UserDetails mainUser = (UserDetails) authentication.getPrincipal();
        String username = mainUser.getUsername();

        String refreshToken = Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(this.key, SignatureAlgorithm.HS256)
                .compact();

        log.info("♻️ Refresh Token generado para usuario: {} con expiración en {} ms", username, refreshExpiration);
        return refreshToken;
    }
    public String generateRefreshTokenFromUsername(String username) {
        return Jwts.builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(new Date(System.currentTimeMillis() + refreshExpiration))
                .signWith(this.key, SignatureAlgorithm.HS256)
                .compact();
    }


    // ✅ Validar token
    public boolean validateToken(String token, UserDetails mainUser) {
        try {
            final String username = extractUsername(token);
            boolean valid = (username.equals(mainUser.getUsername()) && !isTokenExpired(token));

            if (valid) {
                log.debug("✅ Token válido para usuario: {}", username);
            } else {
                log.warn("⚠️ Token inválido o expirado para usuario: {}", username);
            }
            return valid;
        } catch (JwtException e) {
            log.error("❌ Error al validar token: {}", e.getMessage());
            return false;
        }
    }

    public String extractUsername(String token) {
        String username = extractClaim(token, Claims::getSubject);
        log.debug("👤 Username extraído del token: {}", username);
        return username;
    }

    public Date extractExpiration(String token) {
        Date exp = extractClaim(token, Claims::getExpiration);
        log.debug("⏳ Expiración extraída del token: {}", exp);
        return exp;
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private boolean isTokenExpired(String token) {
        boolean expired = extractExpiration(token).before(new Date());
        if (expired) {
            log.warn("⚠️ El token ha expirado.");
        }
        return expired;
    }

    private Claims extractAllClaims(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(this.key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        log.debug("📑 Claims extraídos: {}", claims);
        return claims;
    }
}
