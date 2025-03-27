package org.example.effectivemobilerestapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CommentDto {

    private Long id;

    @NotBlank(message = "Комментарий не может быть null")
    @Size(max = 200, message = "Комментарий не может быть длиннее 200 символов")
    private String text;

    private String author;

    @NotNull(message = "Id задачи не может быть null")
    private Long taskId;

    private LocalDateTime createdAt;
}
