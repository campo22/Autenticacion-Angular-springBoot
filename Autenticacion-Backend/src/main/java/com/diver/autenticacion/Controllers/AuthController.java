package com.diver.autenticacion.Controllers;

import com.diver.autenticacion.Dto.UserDto;
import com.diver.autenticacion.Dto.reques.LoginRequestDTO;
import com.diver.autenticacion.Dto.reques.RegisterRequestDTO;
import com.diver.autenticacion.Dto.response.AuthResponseDTO;
import com.diver.autenticacion.Services.AuthService;
import com.diver.autenticacion.config.CookieUtil;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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

    @PostMapping("/register") // para probar el registro en postman pon: localhost:8080/api/auth/register
    public ResponseEntity<UserDto> registerUser(@Valid @RequestBody RegisterRequestDTO registerRequest) {
        UserDto registeredUser = authService.register(registerRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(registeredUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> authenticateUser(
            @Valid @RequestBody LoginRequestDTO loginRequest,
            HttpServletResponse response
    ) {
        AuthService.AuthResult authResult = authService.login(loginRequest);
        Cookie refreshTokenCookie = cookieUtil.createRefreshTokenCookie(authResult.refreshToken());
        response.addCookie(refreshTokenCookie);
        return ResponseEntity.ok(authResult.authResponse());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refreshAuthenticationToken(
            HttpServletRequest request,
            HttpServletResponse response
    ) {
        AuthService.AuthResult authResult = authService.refresh(request);
        Cookie newRefreshTokenCookie = cookieUtil.createRefreshTokenCookie(authResult.refreshToken());
        response.addCookie(newRefreshTokenCookie);
        return ResponseEntity.ok(authResult.authResponse());
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(HttpServletResponse response) {
        Cookie cleanCookie = cookieUtil.cleanRefreshTokenCookie();
        response.addCookie(cleanCookie);
        return ResponseEntity.ok("Logout exitoso.");
    }
}