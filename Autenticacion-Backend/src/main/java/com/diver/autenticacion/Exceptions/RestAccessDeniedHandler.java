package com.diver.autenticacion.Exceptions;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.time.OffsetDateTime;
import java.util.HashMap;
import java.util.Map;

public class RestAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException)
            throws IOException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN); // 403
        response.setContentType(MediaType.APPLICATION_JSON_VALUE); // Indica que el contenido es JSON

        Map<String, Object> body = new HashMap<>();
        body.put("status", 403);
        body.put("error", "Forbidden");
        body.put("message", "Acceso denegado: no tienes permisos suficientes para realizar esta acción.");
        body.put("path", request.getRequestURI());
        body.put("timestamp", OffsetDateTime.now().toString());

        mapper.writeValue(response.getOutputStream(), body);
    }
}
