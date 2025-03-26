package org.example.effectivemobilerestapi.service.impls;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.example.effectivemobilerestapi.dto.AuthResponseDto;
import org.example.effectivemobilerestapi.dto.UserDto;
import org.example.effectivemobilerestapi.service.interfaces.AuthService;
import org.example.effectivemobilerestapi.service.interfaces.JwtService;
import org.example.effectivemobilerestapi.service.interfaces.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class AuthServiceImpl implements AuthService {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;

    @Override
    public AuthResponseDto register(UserDto newUser) {
        if (userService.existsByEmail(newUser.getEmail())) {
            throw new EntityExistsException("Данный email уже занят");
        }

        UserDto user = userService.saveUser(newUser);

        String token = jwtService.generateToken(user.getEmail());

        return new AuthResponseDto(token);
    }

    @Override
    public AuthResponseDto authenticate(UserDto userDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword()));

        UserDto foundedUser = userService.getUserByEmail(userDto.getEmail());

        String token = jwtService.generateToken(foundedUser.getEmail());

        return new AuthResponseDto(token);
    }
}
