package org.example.effectivemobilerestapi.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;

@Schema(description = "Стандартный формат вывода ошибки в API")
@Data
@AllArgsConstructor
public class ErrorResponseDto {

    @Schema(description = "Сообщение об ошибке")
    private String message;
}
