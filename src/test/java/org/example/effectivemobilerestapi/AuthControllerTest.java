package org.example.effectivemobilerestapi;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.effectivemobilerestapi.controllers.AuthController;
import org.example.effectivemobilerestapi.dto.AuthResponseDto;
import org.example.effectivemobilerestapi.dto.UserDto;
import org.example.effectivemobilerestapi.handlersExceptions.RestHandlerException;
import org.example.effectivemobilerestapi.services.interfaces.AuthService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import jakarta.persistence.EntityExistsException;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class AuthControllerTest {

    @Mock
    private AuthService authService;

    @InjectMocks
    private AuthController authController;

    private MockMvc mockMvc;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController)
                .setControllerAdvice(new RestHandlerException())
                .build();
        objectMapper = new ObjectMapper();
    }

    @Test
    void register_ValidUser_ReturnsAuthResponse() throws Exception {
        UserDto userDto = createValidUserDto();
        AuthResponseDto responseDto = new AuthResponseDto("jwt.token");

        when(authService.register(any(UserDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt.token"));

        verify(authService, times(1)).register(any(UserDto.class));
    }

    @Test
    void register_ExistingEmail_ReturnsConflictWithErrorResponse() throws Exception {
        UserDto existingUser = createValidUserDto();
        String errorMessage = "Пользователь с email 'test@example.com' уже существует";

        when(authService.register(any(UserDto.class)))
                .thenThrow(new EntityExistsException(errorMessage));

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(existingUser)))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(authService, times(1)).register(any(UserDto.class));
    }

    @Test
    void register_InvalidEmail_ReturnsBadRequestWithErrorResponse() throws Exception {
        UserDto invalidUser = createValidUserDto();
        invalidUser.setEmail("invalid-email");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Email должен быть валидным"));

        verify(authService, never()).register(any());
    }

    @Test
    void register_EmptyPassword_ReturnsBadRequestWithErrorResponse() throws Exception {
        UserDto invalidUser = createValidUserDto();
        invalidUser.setPassword("");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidUser)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Пароль не может быть пустым"));

        verify(authService, never()).register(any());
    }

    @Test
    void authenticate_ValidCredentials_ReturnsToken() throws Exception {
        UserDto credentials = createValidUserDto();
        AuthResponseDto response = new AuthResponseDto("auth.token");

        when(authService.authenticate(any(UserDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("auth.token"));

        verify(authService, times(1)).authenticate(any(UserDto.class));
    }

    @Test
    void authenticate_InvalidCredentials_ReturnsUnauthorizedWithErrorResponse() throws Exception {
        UserDto credentials = createValidUserDto();
        String errorMessage = "Неверные учетные данные";

        when(authService.authenticate(any(UserDto.class)))
                .thenThrow(new SecurityException(errorMessage));

        mockMvc.perform(post("/api/auth/authenticate")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(credentials)))
                .andExpect(status().is5xxServerError())
                .andExpect(jsonPath("$.message").value(errorMessage));

        verify(authService, times(1)).authenticate(any(UserDto.class));
    }

    private UserDto createValidUserDto() {
        return new UserDto(
                null,
                "test@example.com",
                "validPassword123",
                null, null, null
        );
    }
}
