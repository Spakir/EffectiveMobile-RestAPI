package org.example.effectivemobilerestapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.effectivemobilerestapi.dto.AuthResponseDto;
import org.example.effectivemobilerestapi.dto.ErrorResponseDto;
import org.example.effectivemobilerestapi.dto.UserDto;
import org.example.effectivemobilerestapi.services.interfaces.AuthService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Validated
@Tag(name = "Authentication API", description = "API для регистрации и аутентификации")
public class AuthController {

    private final AuthService authService;

    @Operation(summary = "Регистрация нового пользователя")
    @ApiResponse(
            responseCode = "200",
            description = "Пользователь успешно зарегистрирован, получение JWT",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AuthResponseDto.class))
    )
    @ApiResponse(
            responseCode = "409",
            description = "Пользователь с заданным email уже занят",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Ошибка с валидацией входных данных",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @PostMapping("/register")
    public AuthResponseDto register(@Valid @RequestBody UserDto newUserDto) {
        return authService.register(newUserDto);
    }

    @Operation(summary = "Аутентификация пользователя")
    @ApiResponse(
            responseCode = "200",
            description = "Аутентификация прошла успешно, Возвращение токена",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = AuthResponseDto.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Аутентификация не прошла успешно",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @PostMapping("/authenticate")
    public AuthResponseDto authenticate(@Valid @RequestBody UserDto singInUserDto) {
        return authService.authenticate(singInUserDto);
    }
}
