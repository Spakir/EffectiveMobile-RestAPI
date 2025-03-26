package org.example.effectivemobilerestapi.service.interfaces;


import org.example.effectivemobilerestapi.dto.AuthResponseDto;
import org.example.effectivemobilerestapi.dto.UserDto;

public interface AuthService {

    AuthResponseDto register(UserDto newUser);

    AuthResponseDto authenticate(UserDto signInUser);
}
