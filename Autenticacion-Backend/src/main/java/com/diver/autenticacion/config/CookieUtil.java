package com.diver.autenticacion.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

/**
 * Utilidad para la gestión de cookies de la aplicación.
 * <p>
 * Proporciona métodos para crear y limpiar cookies HttpOnly,
 * asegurando que los atributos de seguridad (HttpOnly, Secure, SameSite)
 * se apliquen de forma consistente.
 * </p>
 */
@Component
public class CookieUtil {

    /**
     * Nombre de la cookie utilizada para el refresh token.
     * Externalizado para consistencia.
     */
    @Value("${jwt.refreshTokenCookieName}")
    private String refreshTokenCookieName;

    /**
     * Duración del refresh token en segundos.
     * La cookie necesita la duración en segundos, no milisegundos.
     */
    @Value("${jwt.refreshExpiration}")
    private Long refreshTokenDurationMs;

    /**
     * Crea una cookie HttpOnly para el refresh token.
     *
     * @param token El valor del refresh token.
     * @return Un objeto {@link Cookie} configurado con las mejores prácticas de seguridad.
     */
    public Cookie createRefreshTokenCookie(String token) {
        Cookie cookie = new Cookie(refreshTokenCookieName, token);
        cookie.setHttpOnly(true); // ¡CRUCIAL! Previene el acceso desde JavaScript (protección XSS).
        cookie.setSecure(true);   // Solo se envía sobre HTTPS. Poner a false en desarrollo si NO usas SSL.
        cookie.setPath("/api/auth"); // Limita la cookie a los endpoints de autenticación.
        cookie.setMaxAge((int) (refreshTokenDurationMs / 1000)); // La duración debe ser en segundos.
        cookie.setDomain(null); // O el dominio específico si frontend y backend están en subdominios diferentes.
        // cookie.setSameSite("Strict"); // Protección CSRF. 'Strict' es el más seguro. 'Lax' es una alternativa común.
        return cookie;
    }

    /**
     * Crea una cookie de limpieza para invalidar la cookie del refresh token.
     * Esto se usa durante el logout.
     *
     * @return Un objeto {@link Cookie} que le indica al navegador que elimine la cookie existente.
     */
    public Cookie cleanRefreshTokenCookie() {
        Cookie cookie = new Cookie(refreshTokenCookieName, null); // Valor nulo
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setPath("/api/auth");
        cookie.setMaxAge(0); // Expiración inmediata
        cookie.setDomain(null);// null para eliminar la cookie en todos los subdominios
        // cookie.setSameSite("Strict");
        return cookie;
    }

    /**
     * Lee la cookie del refresh token desde la petición HTTP.
     *
     * @param request La petición HTTP entrante.
     * @return El valor del refresh token, o null si la cookie no se encuentra.
     */
    public String readRefreshTokenCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, refreshTokenCookieName);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }
}