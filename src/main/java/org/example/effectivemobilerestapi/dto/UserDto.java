package org.example.effectivemobilerestapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@AllArgsConstructor
public class UserDto {

    private Long id;

    @NotBlank(message = "Почта не может быть пустой")
    @Size(max = 255, message = "Длина почты не может превышать 255 символов")
    @Setter
    @Getter
    private String email;

    @NotBlank(message = "Пароль не может быть пустым")
    @Size(max = 200, message = "Длина пароля не может превышать 200 символов")
    @Setter
    @Getter
    private String password;

    @Setter
    @Getter
    private Role role;
}
