package com.diver.autenticacion.Security;

import com.diver.autenticacion.Repository.UserRepository;
import com.diver.autenticacion.entities.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        log.info("Intentando autenticar usuario: {}", username);
        User user = userRepository.findByUsername( username )
                .orElseThrow( () -> new UsernameNotFoundException( " Usuario no encontrado" ) );


        return new CustomUserDetails( user );
    }
}
