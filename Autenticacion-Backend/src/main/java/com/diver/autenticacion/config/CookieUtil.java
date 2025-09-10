package com.diver.autenticacion.config;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie; // <-- Importante: de Spring
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

@Component
public class CookieUtil {

    @Value("${jwt.refreshTokenCookieName}")
    private String refreshTokenCookieName;

    @Value("${jwt.refreshExpiration}")
    private Long refreshTokenDurationMs;

    /**
     * Crea la cabecera Set-Cookie completa para el refresh token.
     * Utiliza ResponseCookie.Builder para un control total sobre los atributos.
     * @param token El valor del refresh token.
     *
     * <p>Este método construye una {@code ResponseCookie} que representa el refresh token.
     * Configura la cookie con las siguientes propiedades:</p>
     * <ul>
     *     <li>{@code httpOnly(true)}: Impide el acceso a la cookie desde JavaScript, mitigando ataques XSS.</li>
     *     <li>{@code secure(false)}: Permite que la cookie se envíe sobre conexiones HTTP (útil para desarrollo local).</li>
     *     <li>{@code path("/api/auth")}: Define el ámbito de la cookie, limitándola a las rutas bajo {@code /api/auth}.</li>
     *     <li>{@code maxAge(refreshTokenDurationMs / 1000)}: Establece la duración de la cookie en segundos.</li>
     *     <li>{@code sameSite("Lax")}: Ayuda a prevenir ataques CSRF (Cross-Site Request Forgery).</li>
     * </ul>
     * @return Un String formateado como una cabecera HTTP Set-Cookie.
     */
    public String createRefreshTokenCookieHeader(String token) {
        return ResponseCookie.from(refreshTokenCookieName, token)
                .httpOnly(true)
                .secure(false)
                .path("/api/auth")
                .maxAge(refreshTokenDurationMs / 1000)
                .sameSite("Lax")
                .build()
                .toString();
    }

    /**
     * Crea la cabecera Set-Cookie para limpiar/eliminar la cookie del refresh token.
     * @return Un String de cabecera Set-Cookie que invalida la cookie en el navegador.
     */
    public String cleanRefreshTokenCookieHeader() {
        return ResponseCookie.from(refreshTokenCookieName, "") // Valor vacío
                .httpOnly(true)
                .secure(false)
                .path("/api/auth")
                .maxAge(0) // Expiración inmediata
                .sameSite("Lax")
                .build()
                .toString();
    }

    /**
     * Lee el valor de la cookie del refresh token desde la petición entrante.
     * @param request La petición HttpServletRequest.
     * @return El valor del token o null si no se encuentra.
     */
    public String readRefreshTokenCookie(HttpServletRequest request) {
        Cookie cookie = WebUtils.getCookie(request, refreshTokenCookieName);
        if (cookie != null) {
            return cookie.getValue();
        }
        return null;
    }
}