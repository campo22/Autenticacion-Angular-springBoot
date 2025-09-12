package com.diver.autenticacion.Exceptions;


import com.diver.autenticacion.Dto.ErrorResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * Maneja las excepciones de validación de DTOs (causadas por @Valid).
     * Devuelve una respuesta detallada con los campos que fallaron la validación.
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        log.warn("Error de validación de datos de entrada: {}", errors);
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja las excepciones de negocio relacionadas con argumentos inválidos.
     * Ejemplo: "Username ya existe".
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponseDTO> handleIllegalArgumentException(IllegalArgumentException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.BAD_REQUEST.value(),
                ex.getMessage()
        );
        log.warn("Argumento ilegal: {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    /**
     * Maneja las excepciones específicas de refresco de token.
     */
    @ExceptionHandler(TokenRefreshException.class)
    public ResponseEntity<ErrorResponseDTO> handleTokenRefreshException(TokenRefreshException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.FORBIDDEN.value(),
                ex.getMessage()
        );
        log.warn("Fallo en el refresco de token: {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Maneja las excepciones de autenticación de Spring Security.
     * Ejemplo: "Bad credentials".
     */
    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthenticationException(AuthenticationException ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.UNAUTHORIZED.value(),
                "No autenticado: token ausente, inválido o expirado. Inicia sesión o renueva tu token." // Mensaje claro para 401
        );
        log.warn("Fallo de autenticación: {}", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
    }

    /**
     * Maneja los errores de autorización a nivel de método (por @PreAuthorize) y devuelve 403.
     */
    @ExceptionHandler(org.springframework.security.authorization.AuthorizationDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAuthorizationDenied(org.springframework.security.authorization.AuthorizationDeniedException ex,
                                                                      HttpServletRequest request) {
        String message = buildForbiddenMessage(request);
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.FORBIDDEN.value(),
                message
        );
        log.warn("Acceso denegado (método) [{} {}]: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Maneja AccessDeniedException (capa web) y devuelve 403.
     */
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> handleAccessDenied(org.springframework.security.access.AccessDeniedException ex,
                                                               HttpServletRequest request) {
        String message = buildForbiddenMessage(request);
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.FORBIDDEN.value(),
                message
        );
        log.warn("Acceso denegado [{} {}]: {}", request.getMethod(), request.getRequestURI(), ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.FORBIDDEN);
    }

    /**
     * Construye un mensaje 403 más específico según la ruta y el método HTTP.
     * No expone detalles sensibles y mantiene consistencia en la API.
     */
    private String buildForbiddenMessage(HttpServletRequest request) {
        String method = request.getMethod();
        String uri = request.getRequestURI();

        // Personalización para recursos de productos
        if (uri != null && uri.startsWith("/api/products")) {
            if ("DELETE".equalsIgnoreCase(method)) {
                return "Acceso denegado: se requiere el rol ADMIN para eliminar productos.";
            }
            if ("POST".equalsIgnoreCase(method) || "PUT".equalsIgnoreCase(method) || "PATCH".equalsIgnoreCase(method)) {
                return "Acceso denegado: se requieren los roles SUPERVISOR o ADMIN para crear o actualizar productos.";
            }
        }

        // Mensaje por defecto si no hay una regla específica
        return "Acceso denegado: no tienes permisos suficientes para realizar esta acción.";
    }

    /**
     * Captura genérica para cualquier otra excepción no manejada.
     * Es una salvaguarda para evitar exponer stack traces al cliente.
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> handleGlobalException(Exception ex) {
        ErrorResponseDTO errorResponse = new ErrorResponseDTO(
                HttpStatus.INTERNAL_SERVER_ERROR.value(),
                "Ocurrió un error inesperado en el servidor."
        );
        // Logueamos el error completo para depuración interna.
        log.error("Excepción no manejada capturada por GlobalExceptionHandler", ex);
        return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}