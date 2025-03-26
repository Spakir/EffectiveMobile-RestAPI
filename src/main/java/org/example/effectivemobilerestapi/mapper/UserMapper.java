package org.example.effectivemobilerestapi.mapper;

import org.example.effectivemobilerestapi.dto.UserDto;
import org.example.effectivemobilerestapi.entity.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserDto userDto);

    UserDto toUserDto(User user);
}
