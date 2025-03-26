package org.example.effectivemobilerestapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.example.effectivemobilerestapi.enums.Priority;
import org.example.effectivemobilerestapi.enums.Status;

import java.util.List;

@Data
public class TaskDto {

    private Long id;

    @NotBlank(message = "Заголовок не может быть пустым")
    private String header;

    @NotBlank(message = "Описание не должно быть null")
    @Size(max = 200, message = "Описание не может превышать 200 символов")
    private String description;

    private String author;

    private String executor;

    @NotNull(message = "Статус не может быть null")
    private Status status;

    @NotNull(message = "Приоритет не может быть null")
    private Priority priority;

    private List<CommentDto> comments;
}
