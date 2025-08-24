package com.diver.autenticacion.Dto;

import java.time.LocalDateTime;

/**
 * DTO estándar para respuestas de error de la API.
 * @param statusCode El código de estado HTTP.
 * @param message Un mensaje descriptivo del error.
 * @param timestamp La fecha y hora en que ocurrió el error.
 */
public record ErrorResponseDTO(
        int statusCode,
        String message,
        LocalDateTime timestamp
) {
    // Constructor conveniente para no tener que pasar el timestamp cada vez
    public ErrorResponseDTO(int statusCode, String message) {
        this(statusCode, message, LocalDateTime.now());
    }
}