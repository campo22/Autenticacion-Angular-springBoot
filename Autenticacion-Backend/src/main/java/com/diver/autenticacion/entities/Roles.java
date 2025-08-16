package com.diver.autenticacion.entities;

import com.diver.autenticacion.enums.RoleList;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table( name = "roles")
public class Roles {

    @Id
    @GeneratedValue( strategy =  GenerationType.IDENTITY)
    private Long id;

    @Enumerated( EnumType.STRING)
    @Column( nullable = false, unique = true)
    private RoleList name ;

    @ManyToMany( mappedBy = "roles")
    private Set<User> users = new HashSet<>();
}
