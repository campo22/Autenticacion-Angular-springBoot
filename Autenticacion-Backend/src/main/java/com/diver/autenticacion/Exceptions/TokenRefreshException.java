package com.diver.autenticacion.Exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Excepción lanzada cuando ocurre un error en el proceso de refresco de token.
 * La excepción se anota con {@link ResponseStatus} para indicar que debería devolver un código de estado HTTP 403 Forbidden.
 *
 * @author Diver
 * @since 1.0
 */
@ResponseStatus(HttpStatus.FORBIDDEN) // Esto hará que devuelva un 403 Forbidden por defecto
public class TokenRefreshException extends RuntimeException {
    public TokenRefreshException(String token, String message) {
        super(String.format("Fallo para el token [%s]: %s", token, message));
    }
}
// el error 403 significa que el usuario no tiene permisos para acceder a la ruta
// el 401 significa que el usuario no está autenticado
// el 404 significa que no se ha encontrado el recurso
// el 500 significa que ha ocurrido un error en el servidor
// el 400 significa que el usuario ha enviado una petición incorrecta
// el 405 significa que el método no está permitido
// el 409 significa que el recurso ya existe