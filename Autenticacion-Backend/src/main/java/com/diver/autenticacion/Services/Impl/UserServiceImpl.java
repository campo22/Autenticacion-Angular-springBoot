package com.diver.autenticacion.Services.Impl;

import com.diver.autenticacion.Dto.UserDto;
import com.diver.autenticacion.Exceptions.ResourceNotFoundException;
import com.diver.autenticacion.Repository.UserRepository;
import com.diver.autenticacion.Services.UserService;
import com.diver.autenticacion.entities.User;
import com.diver.autenticacion.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;


@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public UserDto findUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el nombre: " + username));

        return userMapper.toUserDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto findUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado con el ID: " + id));

        return userMapper.toUserDTO(user);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAllUsers() {
        List<User> users = userRepository.findAll();

        // Usamos el método que creamos en el mapper para convertir la lista
        return userMapper.toUserDTOList(users);
    }
}