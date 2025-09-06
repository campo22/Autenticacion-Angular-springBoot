package com.diver.autenticacion.Security;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Implementa la interfaz {@link AuthenticationEntryPoint} para personalizar el comportamiento
 * cuando se produce una excepción de autenticación en el punto de entrada de la aplicación.
 * <p>
 * El método {@link #commence(HttpServletRequest, HttpServletResponse, AuthenticationException)}
 * se encarga de enviar una respuesta HTTP con el código de estado 401 (No autorizado) y un mensaje
 * de error personalizado.
 * </p>
 */
@Component
public class JwtEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "No autorizado");
    }
}