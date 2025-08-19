package com.diver.autenticacion.Dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Set;

@Data
@AllArgsConstructor
public class AuthResponseDTO {
    private String token;
    private String refreshToken;
    private String username;
    private String email;
    private Set<String> roles;
}
