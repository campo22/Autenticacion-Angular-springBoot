package com.diver.autenticacion.Repository;

import com.diver.autenticacion.entities.Roles;
import com.diver.autenticacion.enums.RoleList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RolesRepository extends JpaRepository<Roles, Long> {
    Optional <Roles> findByName(RoleList name);
}