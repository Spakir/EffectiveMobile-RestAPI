package org.example.effectivemobilerestapi.services.impls;

import jakarta.persistence.EntityExistsException;
import lombok.RequiredArgsConstructor;
import org.example.effectivemobilerestapi.dto.AuthResponseDto;
import org.example.effectivemobilerestapi.dto.UserDto;
import org.example.effectivemobilerestapi.mappers.UserMapper;
import org.example.effectivemobilerestapi.services.interfaces.AuthService;
import org.example.effectivemobilerestapi.services.interfaces.JwtService;
import org.example.effectivemobilerestapi.services.interfaces.UserService;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

@Service
@RequiredArgsConstructor
@Validated
public class AuthServiceImpl implements AuthService {

    private final UserService userService;

    private final JwtService jwtService;

    private final AuthenticationManager authenticationManager;

    private final UserMapper userMapper;

    private final UserDetailsService userDetailsService;

    @Override
    @Transactional
    public AuthResponseDto register(UserDto newUser) {
        if (userService.existsByEmail(newUser.getEmail())) {
            throw new EntityExistsException("Данный email уже занят");
        }

        UserDto user = userService.saveUser(newUser);
        UserDetails userDetails = userMapper.toUserDetails(user);
        return generateToken(userDetails);
    }

    @Override
    @Transactional
    public AuthResponseDto authenticate(UserDto userDto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(userDto.getEmail(), userDto.getPassword()));

        UserDetails userDetails = userDetailsService.loadUserByUsername(userDto.getEmail());
        return generateToken(userDetails);
    }

    private AuthResponseDto generateToken(UserDetails userDetails){
        return new AuthResponseDto(jwtService.generateToken(userDetails));
    }
}
