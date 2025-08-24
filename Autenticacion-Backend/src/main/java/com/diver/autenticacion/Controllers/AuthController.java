package com.diver.autenticacion.Controllers;

import com.diver.autenticacion.Dto.UserDto;
import com.diver.autenticacion.Dto.reques.LoginRequestDTO;
import com.diver.autenticacion.Dto.reques.RefreshRequestDTO;
import com.diver.autenticacion.Dto.reques.RegisterRequestDTO;
import com.diver.autenticacion.Dto.response.AuthResponseDTO;
import com.diver.autenticacion.Services.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controlador para gestionar los puntos de entrada de autenticación: registro, inicio de sesión y refresco de token.
 * Estos endpoints son públicos y su seguridad se basa en la validación de las credenciales/tokens proporcionados,
 * no en una sesión de autenticación preexistente.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@RequestBody RegisterRequestDTO registerRequest) {
        UserDto registeredUser = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> authenticateUser(@Valid @RequestBody LoginRequestDTO loginRequest) {
        AuthResponseDTO authResponse = authService.login(loginRequest);
        return ResponseEntity.ok(authResponse);
    }



    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshAuthenticationToken(@Valid @RequestBody RefreshRequestDTO request) {
        AuthResponseDTO newAuthResponse = authService.refresh(request);
        return ResponseEntity.ok(newAuthResponse);
    }
}