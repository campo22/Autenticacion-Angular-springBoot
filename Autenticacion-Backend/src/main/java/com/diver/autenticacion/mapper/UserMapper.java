package com.diver.autenticacion.mapper;

import com.diver.autenticacion.Dto.UserDto;
import com.diver.autenticacion.entities.User;

import org.mapstruct.Mapper;


import java.util.List;

@Mapper(componentModel = "spring")
public interface UserMapper {

    // MapStruct mapeará automáticamente los campos con el mismo nombre (id, username, email)
    UserDto toUserDTO(User user);

    // También puedes crear un método para mapear listas enteras
    List<UserDto> toUserDTOList(List<User> users);


}