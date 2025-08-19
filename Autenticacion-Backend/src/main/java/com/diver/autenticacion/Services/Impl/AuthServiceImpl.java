package com.diver.autenticacion.Services.Impl;

import com.diver.autenticacion.Dto.UserDto;
import com.diver.autenticacion.Dto.reques.LoginRequestDTO;
import com.diver.autenticacion.Dto.reques.RefreshRequestDTO;
import com.diver.autenticacion.Dto.reques.RegisterRequestDTO;
import com.diver.autenticacion.Dto.response.AuthResponseDTO;
import com.diver.autenticacion.Exceptions.TokenRefreshException;
import com.diver.autenticacion.Repository.RolesRepository;
import com.diver.autenticacion.Repository.UserRepository;
import com.diver.autenticacion.Security.CustomUserDetails;
import com.diver.autenticacion.Services.AuthService;
import com.diver.autenticacion.Services.CustomUserDetailsService;
import com.diver.autenticacion.config.JwtUtils;
import com.diver.autenticacion.entities.Roles;
import com.diver.autenticacion.entities.User;
import com.diver.autenticacion.enums.RoleList;
import com.diver.autenticacion.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {


    private final UserRepository userRepository;
    private final RolesRepository roleRepository;

    // Dependencias de Seguridad y JWT
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final CustomUserDetailsService userDetailsService;

    // Dependencia de Mapeo
    private final UserMapper userMapper;

    /**
     * Registra un nuevo usuario con el rol por defecto 'ROLE_USER'.
     * La operación es transaccional para garantizar la integridad de los datos.
     *
     * @param registerRequest DTO con los datos del nuevo usuario.
     * @return UserDTO con la información pública del usuario recién creado.
     */
    @Override
    @Transactional
    public UserDto register(RegisterRequestDTO registerRequest) {
        log.info("Iniciando registro para el usuario: {}", registerRequest.getUsername());

        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new IllegalArgumentException("Error: El nombre de usuario ya está en uso.");
        }
        if (userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new IllegalArgumentException("Error: El email ya está en uso.");
        }

        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(passwordEncoder.encode(registerRequest.getPassword()));

        Set<Roles> roles = new HashSet<>();
        Roles userRole = roleRepository.findByName(RoleList.ROLE_USER)
                .orElseThrow(() -> new RuntimeException("Error: Rol por defecto 'ROLE_USER' no encontrado en la base de datos."));
        roles.add(userRole);
        user.setRoles(roles);

        User savedUser = userRepository.save(user);
        log.info("Usuario {} registrado exitosamente con id {}", savedUser.getUsername(), savedUser.getId());

        return userMapper.toUserDTO(savedUser);
    }

    /**
     * Autentica a un usuario y le proporciona un set de tokens de acceso y refresco.
     *
     * @param loginRequest DTO con las credenciales del usuario.
     * @return AuthResponseDTO que contiene los tokens y la información del usuario.
     */
    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO loginRequest) {
        log.info("Intentando autenticar al usuario: {}", loginRequest.getUsername());
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);


        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        

        String accessToken = jwtUtils.generateToken(authentication);
        String refreshToken = jwtUtils.generateRefreshToken(authentication);

        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        log.info("Usuario {} autenticado correctamente.", userDetails.getUsername());

        return new AuthResponseDTO(
                accessToken,
                refreshToken,
                userDetails.getUsername(),
                ((CustomUserDetails) userDetails).getEmail(),
                roles
        );
    }

    /**
     * Refresca un token de acceso expirado utilizando un token de refresco válido.
     * Implementa la rotación de tokens de refresco por seguridad.
     *
     * @param refreshRequest DTO que contiene el token de refresco.
     * @return AuthResponseDTO con un nuevo token de acceso y un nuevo token de refresco.
     */
    @Override
    @Transactional(readOnly = true)
    public AuthResponseDTO refresh(RefreshRequestDTO refreshRequest) {
        String requestRefreshToken = refreshRequest.refreshToken();
        log.info("Intentando refrescar token.");

        // 1. Validar la integridad criptográfica del token.
        if (!jwtUtils.validateToken(requestRefreshToken)) {
            throw new TokenRefreshException(requestRefreshToken, "Refresh token inválido, expirado o malformado.");
        }

        // 2. Extraer username y verificar que el usuario sigue siendo válido en nuestro sistema.
        String username = jwtUtils.extractUsername(requestRefreshToken);
        UserDetails userDetails;
        try {
            userDetails = this.userDetailsService.loadUserByUsername(username);
        } catch (Exception e) {
            throw new TokenRefreshException(requestRefreshToken, "El usuario asociado al token ya no es válido.");
        }

        // 3. Generar un nuevo juego de tokens (rotación).
        String newAccessToken = jwtUtils.generateTokenFromUsername(userDetails.getUsername());
        String newRefreshToken = jwtUtils.generateRefreshTokenFromUsername(userDetails.getUsername());

        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        log.info("Tokens refrescados exitosamente para el usuario: {}", username);

        return new AuthResponseDTO(
                newAccessToken,
                newRefreshToken,
                userDetails.getUsername(),
                ((CustomUserDetails) userDetails).getEmail(),
                roles
        );
    }
}