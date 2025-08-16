package com.diver.autenticacion.Dto;

import lombok.Data;

import java.util.Set;

@Data
public class UserDto {


    private Long id;
    private String username;
    private String password;
    private String email;
    private Set<String> roles ;
}
