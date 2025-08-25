package com.diver.autenticacion.config;

import com.diver.autenticacion.Services.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    /**
     * El corazón de la autenticación por token. Este filtro se ejecuta una vez por cada petición.
     * Su trabajo es inspeccionar la cabecera 'Authorization', validar el JWT si existe,
     * y establecer la identidad del usuario en el SecurityContext de Spring.
     */
    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        // 1. Extraer el token de la cabecera.
        final String jwt = extractJwtFromRequest(request);

        if (jwt == null) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            // 2. Extraer el username del token.
            final String username = jwtUtils.extractUsername(jwt);

            // 3. Si tenemos username y el usuario aún no está autenticado en el contexto actual.
            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

                //  Esta es la validación final que comprueba
                // que el token es criptográficamente válido y pertenece al usuario.
                if (jwtUtils.validateToken(jwt) && userDetails.getUsername().equals(username)) {
                    // Si el token es válido, creamos un objeto de autenticación
                    UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(
                            userDetails,
                            null,
                            userDetails.getAuthorities()
                    );
                    authToken.setDetails(
                            new WebAuthenticationDetailsSource().buildDetails(request)
                    );

                    // ¡Y lo establecemos en el SecurityContext! Spring Security ahora sabe quién es el usuario.
                    SecurityContextHolder.getContext().setAuthentication(authToken);
                    log.debug("Usuario '{}' autenticado exitosamente.", username);
                }
            }
        } catch (ExpiredJwtException e) {
            log.warn("El token JWT ha expirado: {}. La petición será rechazada.", e.getMessage());
            // No hacemos nada más, dejamos que la cadena de seguridad rechace la petición.
        } catch (Exception e) {
            log.error("Error durante la autenticación con JWT: {}", e.getMessage());
            // En caso de otros errores, tampoco autenticamos.
        }

        // 5. Continuar con la cadena de filtros.
        filterChain.doFilter(request, response);
    }

    /**
     * Método de utilidad para extraer el token JWT de la cabecera 'Authorization'.
     * @param request La petición HTTP.
     * @return El token como un String, o null si no se encuentra.
     */
    private String extractJwtFromRequest(HttpServletRequest request) {
        final String authHeader = request.getHeader("Authorization");
        if (StringUtils.hasText(authHeader) && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}