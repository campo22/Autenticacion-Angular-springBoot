package com.diver.autenticacion.Services;

import com.diver.autenticacion.Dto.UserDto;
import com.diver.autenticacion.Dto.reques.LoginRequestDTO;
import com.diver.autenticacion.Dto.reques.RefreshRequestDTO;
import com.diver.autenticacion.Dto.reques.RegisterRequestDTO;
import com.diver.autenticacion.Dto.response.AuthResponseDTO;

public interface AuthService {

    UserDto register(RegisterRequestDTO registerRequestDTO);

    AuthResponseDTO login(LoginRequestDTO loginRequestDTO);

    AuthResponseDTO refresh(RefreshRequestDTO refreshRequestDTO);

}
