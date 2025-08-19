package com.diver.autenticacion.Services;

import com.diver.autenticacion.Repository.UserRepository;
import com.diver.autenticacion.Security.CustomUserDetails;
import com.diver.autenticacion.entities.User;
import lombok.RequiredArgsConstructor; // Usa esta
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service; // Anota como Service

@Service // <-- ¡Añadido!
@RequiredArgsConstructor // <-- ¡Añadido!
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository; // <-- ¡Declarado como final!

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado con el nombre: " + username));
        return new CustomUserDetails(user); // Asumiendo que CustomUserDetails implementa UserDetails
    }
}