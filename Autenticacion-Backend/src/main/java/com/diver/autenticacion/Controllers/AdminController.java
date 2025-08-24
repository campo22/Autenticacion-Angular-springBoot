package com.diver.autenticacion.Controllers;

;
import com.diver.autenticacion.Dto.UserDto;
import com.diver.autenticacion.Services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminController {

    private final UserService userService;

    /**
     * Endpoint para obtener una lista de todos los usuarios del sistema.
     * @PreAuthorize("hasRole('ADMIN')") restringe el acceso solo a usuarios con el rol 'ADMIN'.
     * @return Una lista de DTOs de usuario.
     */
    @GetMapping("/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<UserDto>> getAllUsers() {
        List<UserDto> users = userService.findAllUsers();
        return ResponseEntity.ok(users);
    }

    /**
     * Endpoint para que un administrador vea el perfil de cualquier usuario por su ID.
     * @PreAuthorize("hasRole('ADMIN')") asegura que solo los administradores pueden invocar este método.
     * @param userId El ID del usuario a buscar.
     * @return El DTO del usuario encontrado.
     */
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UserDto> getUserById(@PathVariable Long userId) {
        UserDto userDTO = userService.findUserById(userId);
        return ResponseEntity.ok(userDTO);
    }
}