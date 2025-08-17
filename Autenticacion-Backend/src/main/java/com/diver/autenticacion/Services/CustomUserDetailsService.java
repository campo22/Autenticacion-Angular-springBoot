package com.diver.autenticacion.Services;

import com.diver.autenticacion.Repository.UserRepository;
import com.diver.autenticacion.Security.CustomUserDetails;
import com.diver.autenticacion.entities.User;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Slf4j

@NoArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private  UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username) // Usa el repositorio inyectado
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
        return new CustomUserDetails(user);
    }
}