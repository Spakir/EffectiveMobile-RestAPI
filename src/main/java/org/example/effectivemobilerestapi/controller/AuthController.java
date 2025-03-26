package org.example.effectivemobilerestapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.effectivemobilerestapi.dto.AuthResponseDto;
import org.example.effectivemobilerestapi.dto.UserDto;
import org.example.effectivemobilerestapi.service.interfaces.AuthService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    public AuthResponseDto register(@RequestBody UserDto newUserDto){
        return authService.register(newUserDto);
    }

    @PostMapping("/authenticate")
    public AuthResponseDto authenticate(@RequestBody UserDto singInUserDto){
        return authService.authenticate(singInUserDto);
    }

}
