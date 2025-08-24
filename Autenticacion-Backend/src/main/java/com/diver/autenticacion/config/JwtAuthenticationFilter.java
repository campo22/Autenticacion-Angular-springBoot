package com.diver.autenticacion.config;

import com.diver.autenticacion.Services.CustomUserDetailsService;
import io.jsonwebtoken.ExpiredJwtException; // Importante para manejar la expiración limpiamente
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");

        if (!StringUtils.hasText(authHeader) || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return; // Si no hay token Bearer, continuamos con la cadena de filtros.
        }

        final String jwt = authHeader.substring(7);
        String username = null;

        try {
            username = jwtUtils.extractUsername(jwt);
        } catch (ExpiredJwtException e) {
            log.warn("El token JWT ha expirado: {}", e.getMessage());

        } catch (Exception e) {
            log.error("No se pudo extraer el username del token JWT: {}", e.getMessage());
        }


        // Si tenemos un username y no hay una autenticación previa en el contexto de seguridad...
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {

            // Cargamos los detalles del usuario desde la base de datos.
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);


            if (jwtUtils.validateToken(jwt) && userDetails.getUsername().equals(username)) {

                // Si todo es válido, creamos el objeto de autenticación.
                UsernamePasswordAuthenticationToken authenticationToken =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities());

                // Adjuntamos detalles de la petición web al objeto de autenticación.
                authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // ¡Establecemos la autenticación en el contexto de seguridad de Spring!
                // A partir de este punto, el usuario está "logueado" para esta petición.
                SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                log.debug("Usuario '{}' autenticado exitosamente y contexto de seguridad establecido.", username);
            }
        }

        // Continuamos con el resto de la cadena de filtros.
        filterChain.doFilter(request, response);
    }
}