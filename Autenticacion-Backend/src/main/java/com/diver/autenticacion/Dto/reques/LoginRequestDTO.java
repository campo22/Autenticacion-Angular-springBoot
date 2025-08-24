package com.diver.autenticacion.Dto.reques;

import jakarta.validation.constraints.Size;
import lombok.Data;
import jakarta.validation.constraints.NotBlank;


@Data
public class LoginRequestDTO {
    
    @NotBlank (message = "El nombre de usuario es obligatorio.")
    private String username;
    
    @NotBlank (message = "La contraseña es obligatoria.")
    @Size(min = 3, message = "La contraseña debe tener al menos 3 caracteres.")
    private String password;
}
