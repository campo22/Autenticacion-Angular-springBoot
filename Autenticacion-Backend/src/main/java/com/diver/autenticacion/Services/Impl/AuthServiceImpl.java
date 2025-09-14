package com.diver.autenticacion.Services.Impl;

import com.diver.autenticacion.Dto.UserDto;
import com.diver.autenticacion.Dto.reques.LoginRequestDTO;
import com.diver.autenticacion.Dto.reques.RegisterRequestDTO;
import com.diver.autenticacion.Dto.response.AuthResponseDTO;
import com.diver.autenticacion.Exceptions.TokenRefreshException;
import com.diver.autenticacion.Repository.RoleRepository;
import com.diver.autenticacion.Repository.UserRepository;
import com.diver.autenticacion.Security.CustomUserDetails;
import com.diver.autenticacion.Services.AuthService;
import com.diver.autenticacion.Services.CustomUserDetailsService;
import com.diver.autenticacion.config.CookieUtil;
import com.diver.autenticacion.config.JwtUtils;

import com.diver.autenticacion.entities.Role;
import com.diver.autenticacion.entities.User;

import com.diver.autenticacion.enums.ERole;
import com.diver.autenticacion.mapper.UserMapper;

import jakarta.servlet.http.HttpServletRequest;
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


import java.util.Set;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final UserMapper userMapper;
    private final CustomUserDetailsService userDetailsService;
    private final CookieUtil cookieUtil;

    @Override
    @Transactional
    public UserDto register(RegisterRequestDTO registerRequest) {
        // ... (Lógica de registro sin cambios, como la teníamos en la versión final)
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

        ERole userRoleEnum;
        if (registerRequest.getRole() != null && !registerRequest.getRole().isBlank()) {
            String requestedRole = registerRequest.getRole().toUpperCase();
            if (requestedRole.equals("SUPERVISOR")) {
                userRoleEnum = ERole.ROLE_SUPERVISOR;
            } else if (requestedRole.equals("USER")) {
                userRoleEnum = ERole.ROLE_USER;
            } else {
                throw new IllegalArgumentException("Error: El rol '" + registerRequest.getRole() + "' no es válido o no está permitido para el registro.");
            }
        } else {
            userRoleEnum = ERole.ROLE_USER;
        }

        Role userRole = roleRepository.findByName(userRoleEnum)
                .orElseThrow(() -> new RuntimeException("Error: El rol " + userRoleEnum.name() + " no fue encontrado en la base de datos."));

        user.setRoles(Set.of(userRole));
        User savedUser = userRepository.save(user);
        log.info("Usuario {} registrado exitosamente con el rol {}", savedUser.getUsername(), userRoleEnum.name());
        return userMapper.toUserDTO(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResult login(LoginRequestDTO loginRequest) {
        // Registra el intento de autenticación para el usuario especificado.
        log.info("Intentando autenticar al usuario: {}", loginRequest.getUsername());

        // Autentica al usuario utilizando el AuthenticationManager.
        // Se crea un UsernamePasswordAuthenticationToken con el nombre de usuario y la contraseña
        // proporcionados en la solicitud de login.
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

        // Establece el objeto Authentication en el SecurityContextHolder.
        // Esto es crucial para que Spring Security reconozca que el usuario está autenticado
        // en el contexto actual de la aplicación.
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Obtiene los detalles del usuario autenticado.
        // Se espera que el principal de la autenticación sea una instancia de CustomUserDetails.
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        // Genera un token de acceso JWT para el usuario autenticado.
        // Este token se utilizará para futuras solicitudes autenticadas.
        String accessToken = jwtUtils.generateToken(authentication);
        // Genera un refresh token JWT.
        // Este token se utilizará para obtener nuevos tokens de acceso sin necesidad de reautenticación.
        String refreshToken = jwtUtils.generateRefreshToken(authentication);

        // Extrae los roles (autoridades) del usuario autenticado y los convierte en un conjunto de cadenas.
        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        // Construye el objeto AuthResponseDTO que contiene la información del usuario autenticado
        // y el token de acceso.
        AuthResponseDTO authResponse = new AuthResponseDTO(
                accessToken,
                userDetails.getUsername(),
                userDetails.getEmail(),
                roles
        );

        // Registra que el usuario ha sido autenticado correctamente.
        log.info("Usuario {} autenticado correctamente.", userDetails.getUsername());
        // Devuelve un objeto AuthResult que encapsula la respuesta de autenticación y el refresh token.
        return new AuthResult(authResponse, refreshToken);
    }

    @Override
    @Transactional(readOnly = true)
    public AuthResult refresh(HttpServletRequest request) {

        String requestRefreshToken = cookieUtil.readRefreshTokenCookie(request);

        if (requestRefreshToken == null) {
            throw new TokenRefreshException(null, "No se encontró la cookie de refresh token.");
        }

        log.info("Intentando refrescar token desde cookie.");

        if (!jwtUtils.validateToken(requestRefreshToken)) {
            throw new TokenRefreshException(requestRefreshToken, "Refresh token en cookie es inválido, expirado o malformado.");
        }

        String username = jwtUtils.extractUsername(requestRefreshToken);
        UserDetails userDetails;

        try {
            userDetails = this.userDetailsService.loadUserByUsername(username);
        } catch (Exception e) {
            throw new TokenRefreshException(requestRefreshToken, "El usuario asociado al token ya no es válido.");
        }

        String newAccessToken = jwtUtils.generateTokenFromUsername(userDetails.getUsername());
        String newRefreshToken = jwtUtils.generateRefreshTokenFromUsername(userDetails.getUsername());

        Set<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        AuthResponseDTO authResponse = new AuthResponseDTO(
                newAccessToken,
                userDetails.getUsername(),
                ((CustomUserDetails) userDetails).getEmail(),
                roles
        );

        log.info("Tokens refrescados exitosamente para el usuario: {}", username);
        return new AuthResult(authResponse, newRefreshToken);
    }
}