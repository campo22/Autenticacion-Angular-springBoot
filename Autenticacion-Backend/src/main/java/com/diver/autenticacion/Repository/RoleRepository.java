package com.diver.autenticacion.Repository;

import com.diver.autenticacion.entities.Role;
import com.diver.autenticacion.enums.ERole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional <Role> findByName(ERole name);
}