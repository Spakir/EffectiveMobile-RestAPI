package org.example.effectivemobilerestapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.example.effectivemobilerestapi.enums.Role;

import java.util.List;

@AllArgsConstructor
@Data
public class UserDto {
    private Long id;

    @NotBlank(message = "Почта не может быть пустой")
    @Size(max = 255, message = "Длина почты не может превышать 255 символов")
    @Pattern(regexp = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9]+\\.[a-z]+$", message = "Email должен быть валидным")
    private String email;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(max = 200, message = "Длина пароля не может превышать 200 символов")
    private String password;

    private Role role;

    private List<TaskDto> createdTasks;

    private List<TaskDto> assignedTasks;
}
