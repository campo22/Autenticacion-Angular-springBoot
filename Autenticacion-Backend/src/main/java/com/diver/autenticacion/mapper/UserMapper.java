package com.diver.autenticacion.mapper;

import com.diver.autenticacion.Dto.UserDto;
import com.diver.autenticacion.entities.User;
import com.diver.autenticacion.entities.Role;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // MapStruct mapeará automáticamente los campos con el mismo nombre (id, username, email)
    @Mapping(target = "roles", source = "roles")
    UserDto toUserDTO(User user);

    // También puedes crear un método para mapear listas enteras
    List<UserDto> toUserDTOList(List<User> users);
    
    // Método personalizado para mapear Set<Roles> a Set<String>
    default Set<String> mapRolesToStrings(Set<Role> roles) {
        if (roles == null) {
            return null;
        }
        return roles.stream()
                .map(Role::getName)
                .map(Enum::name)
                .collect(Collectors.toSet());
    }
}