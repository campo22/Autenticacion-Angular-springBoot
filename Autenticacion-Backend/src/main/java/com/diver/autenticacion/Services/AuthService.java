package com.diver.autenticacion.Services;

import com.diver.autenticacion.Dto.UserDto;
import com.diver.autenticacion.Dto.reques.LoginRequestDTO;
import com.diver.autenticacion.Dto.reques.RefreshRequestDTO;
import com.diver.autenticacion.Dto.reques.RegisterRequestDTO;
import com.diver.autenticacion.Dto.response.AuthResponseDTO;
import jakarta.servlet.http.HttpServletRequest;

public interface AuthService {

    // el resultado de la autenticación es un AuthResponseDTO y un refreshToken
    // el record AuthResult es un registro de dos campos authResponse y refreshToken
    record AuthResult(AuthResponseDTO authResponse, String refreshToken) {}

    UserDto register(RegisterRequestDTO registerRequestDTO);

    AuthResult login(LoginRequestDTO loginRequestDTO);

    AuthResult refresh(HttpServletRequest request);

}
