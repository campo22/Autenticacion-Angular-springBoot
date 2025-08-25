package com.diver.autenticacion.runner;

import com.diver.autenticacion.Repository.RoleRepository;
import com.diver.autenticacion.entities.Role;
import com.diver.autenticacion.enums.ERole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("Inicializando roles en la base de datos...");

        // Itera sobre todos los valores del Enum RoleList
        Arrays.stream(ERole.values()).forEach(erole -> {
            // Comprueba si el rol ya existe en la base de datos
            if (!roleRepository.findByName(erole).isPresent()) {
                // Si no existe, lo crea y lo guarda
                roleRepository.save(new Role(erole));
                log.info("Rol '{}' creado.", erole.name());
            }
        });

        log.info("Inicialización de roles completada.");
    }
}