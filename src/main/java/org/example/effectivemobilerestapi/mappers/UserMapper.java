package org.example.effectivemobilerestapi.mappers;

import org.example.effectivemobilerestapi.enums.Role;
import org.example.effectivemobilerestapi.dto.UserDto;
import org.example.effectivemobilerestapi.entities.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Mapper(componentModel = "spring")
public interface UserMapper {

    User toUser(UserDto userDto);

    UserDto toUserDto(User user);

    @Mapping(target = "username", source = "email")
    @Mapping(target = "authorities", expression = "java(mapAuthority(userDto.getRole()))")
    default UserDetails toUserDetails(UserDto userDto) {
        return org.springframework.security.core.userdetails.User
                .withUsername(userDto.getEmail())
                .password(userDto.getPassword())
                .authorities(userDto.getRole().name())
                .build();
    }

    default Collection<? extends GrantedAuthority> mapAuthority(Role role) {
        return Collections.singletonList(new SimpleGrantedAuthority(role.name()));
    }
}
