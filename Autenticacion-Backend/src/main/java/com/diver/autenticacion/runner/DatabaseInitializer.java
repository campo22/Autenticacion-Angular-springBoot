package com.diver.autenticacion.runner;


import com.diver.autenticacion.Repository.ProductRepository;
import com.diver.autenticacion.Repository.RoleRepository;
import com.diver.autenticacion.Repository.UserRepository;
import com.diver.autenticacion.entities.Product;
import com.diver.autenticacion.entities.Role;
import com.diver.autenticacion.entities.User;

import com.diver.autenticacion.enums.ERole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Set;

/**
 * Este componente se ejecuta una vez al arrancar la aplicación.
 * Su propósito es inicializar la base de datos con datos maestros (como roles)
 * y datos de prueba (como un usuario administrador y productos de ejemplo)
 * para facilitar el desarrollo y las pruebas.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class DatabaseInitializer implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * El método principal que se ejecuta al iniciar. Orquesta la secuencia de inicialización.
     */
    @Override
    @Transactional // Envolver toda la inicialización en una única transacción
    public void run(String... args) throws Exception {
        log.info("Iniciando la inicialización de la base de datos...");

        seedRoles();
        createAdminUser();
        createSupervisorUser(); // Opcional: crear un supervisor para pruebas
        createUser(); // Opcional: crear un usuario normal para pruebas
        seedProducts();

        log.info("Inicialización de la base de datos completada.");
    }

    /**
     * Crea los roles definidos en el Enum ERole si no existen en la base de datos.
     * Este método es idempotente, lo que significa que se puede ejecutar de forma segura
     * varias veces sin causar efectos secundarios no deseados.
     */
    private void seedRoles() {
        log.info("Sembrando roles...");
        Arrays.stream(ERole.values()).forEach(erole -> {
            if (!roleRepository.findByName(erole).isPresent()) {
                roleRepository.save(new Role(erole));
                log.info("Rol '{}' creado.", erole.name());
            }
        });
    }

    /**
     * Crea un usuario administrador por defecto si no existe.
     * Esto es crucial para poder gestionar la aplicación desde el principio.
     */
    private void createAdminUser() {
        String adminUsername = "admin";
        if (userRepository.findByUsername(adminUsername).isEmpty()) {
            log.info("Creando usuario administrador: {}", adminUsername);

            Role adminRole = roleRepository.findByName(ERole.ROLE_ADMIN)
                    .orElseThrow(() -> new RuntimeException("Error: Rol 'ROLE_ADMIN' no encontrado."));

            User adminUser = User.builder()
                    .username(adminUsername)
                    .email("admin@mi-app.com")
                    .password(passwordEncoder.encode("12345")) // ¡Cambiar en producción!
                    .roles(Set.of(adminRole))
                    .build();

            userRepository.save(adminUser);
        }
    }

    /**
     * Crea un usuario supervisor por defecto si no existe. Útil para probar permisos.
     */
    private void createSupervisorUser() {
        String supervisorUsername = "supervisor";
        if (userRepository.findByUsername(supervisorUsername).isEmpty()) {
            log.info("Creando usuario supervisor: {}", supervisorUsername);

            Role supervisorRole = roleRepository.findByName(ERole.ROLE_SUPERVISOR)
                    .orElseThrow(() -> new RuntimeException("Error: Rol 'ROLE_SUPERVISOR' no encontrado."));

            User supervisorUser = User.builder()
                    .username(supervisorUsername)
                    .email("supervisor@mi-app.com")
                    .password(passwordEncoder.encode("12345"))
                    .roles(Set.of(supervisorRole))
                    .build();

            userRepository.save(supervisorUser);
        }
    }

    /**
     * Crea un usuario normal por defecto si no existe. Útil para probar permisos.
     */
    private void createUser() {
        String userUsername = "user";
        if (userRepository.findByUsername(userUsername).isEmpty()) {
            log.info("Creando usuario normal: {}", userUsername);

            Role userRole = roleRepository.findByName(ERole.ROLE_USER)
                    .orElseThrow(() -> new RuntimeException("Error: Rol 'ROLE_USER' no encontrado."));

            User regularUser = User.builder()
                    .username(userUsername)
                    .email("user@mi-app.com")
                    .password(passwordEncoder.encode("userpass123"))
                    .roles(Set.of(userRole))
                    .build();

            userRepository.save(regularUser);
        }
    }

    /**
     * Crea una lista de productos de ejemplo si la tabla de productos está vacía.
     */
    private void seedProducts() {
        log.info("Verificando datos de productos...");
        if (productRepository.count() == 0) {
            log.info("Sembrando productos de ejemplo...");
            List<Product> products = List.of(
                    Product.builder().name("Laptop Pro X").description("Potente laptop para desarrolladores").price(1599.99).build(),
                    Product.builder().name("Teclado Mecánico RGB").description("Switches Cherry MX Blue, totalmente personalizable").price(125.50).build(),
                    Product.builder().name("Monitor UltraWide 4K").description("34 pulgadas, para una inmersión total").price(799.00).build(),
                    Product.builder().name("Mouse Ergonómico Vertical").description("Diseñado para reducir la tensión en la muñeca").price(89.95).build(),
                    Product.builder().name("Dockstation USB-C").description("Conecta todos tus periféricos con un solo cable").price(199.99).build(),
                    Product.builder().name("Auriculares con Cancelación de Ruido").description("Sumérgete en tu código o música sin distracciones").price(299.00).build(),
                    Product.builder().name("Webcam 1080p Profesional").description("Para reuniones virtuales nítidas y claras").price(75.00).build()
            );
            productRepository.saveAll(products);
            log.info("Se han sembrado {} productos.", products.size());
        }
    }
}