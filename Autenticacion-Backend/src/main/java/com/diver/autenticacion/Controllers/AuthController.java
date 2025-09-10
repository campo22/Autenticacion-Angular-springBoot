package com.diver.autenticacion.Controllers;

import com.diver.autenticacion.Dto.UserDto;
import com.diver.autenticacion.Dto.reques.LoginRequestDTO;
import com.diver.autenticacion.Dto.reques.RegisterRequestDTO;
import com.diver.autenticacion.Dto.response.AuthResponseDTO;
import com.diver.autenticacion.Services.AuthService;
import com.diver.autenticacion.config.CookieUtil;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders; // <-- Importante: para Set-Cookie
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final CookieUtil cookieUtil;

    @PostMapping("/register")
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        UserDto registeredUser = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> authenticateUser(
            @Valid @RequestBody LoginRequestDTO loginRequest
    ) {
        AuthService.AuthResult authResult = authService.login(loginRequest);

        // 1. Generamos la cabecera completa
        String cookieHeader = cookieUtil.createRefreshTokenCookieHeader(authResult.refreshToken());

        // 2. Construimos la respuesta añadiendo la cabecera
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieHeader)
                .body(authResult.authResponse());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshAuthenticationToken(
            HttpServletRequest request
    ) {
        // En tu AuthService, asegúrate de que el método se llame 'refreshToken'
        AuthService.AuthResult authResult = authService.refresh(request);

        String cookieHeader = cookieUtil.createRefreshTokenCookieHeader(authResult.refreshToken());

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieHeader)
                .body(authResult.authResponse());
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser() {
        String cookieHeader = cookieUtil.cleanRefreshTokenCookieHeader();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookieHeader)
                .body("Logout exitoso.");
    }
}