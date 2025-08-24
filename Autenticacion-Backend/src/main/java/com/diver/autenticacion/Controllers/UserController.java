package com.diver.autenticacion.Controllers;

import com.diver.autenticacion.Dto.UserDto;
import com.diver.autenticacion.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * Endpoint para que un usuario autenticado obtenga su propia información de perfil.
     * @PreAuthorize("isAuthenticated()") asegura que solo los usuarios logueados pueden acceder.
     * @param principal Objeto inyectado por Spring Security que contiene los detalles del usuario autenticado.
     * @return El DTO del usuario actual.
     */
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserDto> getCurrentUser(Principal principal) {
        // Principal.getName() devuelve el 'username' del token.
        UserDto userDTO = userService.findUserByUsername(principal.getName());
        return ResponseEntity.ok(userDTO);
    }
}