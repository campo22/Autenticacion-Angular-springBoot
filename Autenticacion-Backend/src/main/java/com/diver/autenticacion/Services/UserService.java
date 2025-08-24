package com.diver.autenticacion.Services;

import com.diver.autenticacion.Dto.UserDto;

import java.util.List;

/**
 * Interfaz de servicio para operaciones de negocio relacionadas con los usuarios.
 * Define el contrato para la gestión de usuarios, como búsquedas y listados.
 * La creación de usuarios (registro) es manejada por AuthService.
 */
public interface UserService {

    /**
     * Busca un usuario por su nombre de usuario (username).
     *
     * @param username El nombre de usuario a buscar.
     * @return El UserDTO correspondiente al usuario encontrado.
     * @throws org.springframework.security.core.userdetails.UsernameNotFoundException si el usuario no existe.
     */
    UserDto findUserByUsername(String username);

    /**
     * Busca un usuario por su ID.
     * Este método es típicamente para uso administrativo.
     *
     * @param id El ID del usuario a buscar.
     * @return El UserDTO correspondiente al usuario encontrado.
     * @throws com.diver.autenticacion.Exceptions.ResourceNotFoundException si el usuario no existe.
     */
    UserDto findUserById(Long id);

    /**
     * Devuelve una lista de todos los usuarios registrados en el sistema.
     * Este método es típicamente para uso administrativo.
     *
     * @return Una lista de UserDTOs.
     */
    List<UserDto> findAllUsers();


}