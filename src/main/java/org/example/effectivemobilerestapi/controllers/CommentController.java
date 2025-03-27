package org.example.effectivemobilerestapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.effectivemobilerestapi.dto.CommentDto;
import org.example.effectivemobilerestapi.dto.ErrorResponseDto;
import org.example.effectivemobilerestapi.services.interfaces.CommentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
@Validated
@Tag(name = "Comment API", description = "API для работы с комментариями к задачам")
public class CommentController {

    private final CommentService commentService;

    @Operation(summary = "Создание нового комментария к задаче")
    @ApiResponse(
            responseCode = "200",
            description = "Комментарий успешно создан",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CommentDto.class))
    )
    @ApiResponse(
            responseCode = "403",
            description = "У пользователя недостаточно прав для данного действия",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Ошибка валидации данных",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @PostMapping("/new")
    public CommentDto saveComment(@AuthenticationPrincipal UserDetails user,
                                  @Valid @RequestBody CommentDto commentDto) {

        return commentService.saveComment(user, commentDto);
    }

    @Operation(summary = "Получение комментария по ID")
    @ApiResponse(
            responseCode = "200",
            description = "Комментарий успешно найден",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CommentDto.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Комментарий с данным ID не был найден",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @GetMapping("/{id}")
    public CommentDto getCommentById(@AuthenticationPrincipal UserDetails user,
                                     @PathVariable("id") Long id) {

        return commentService.getCommentById(user, id);
    }

    @Operation(summary = "Получение списка комментариев к определенной группе")
    @ApiResponse(
            responseCode = "200",
            description = "Список успешно получен",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CommentDto.class))
    )
    @ApiResponse(
            responseCode = "403",
            description = "У пользователя недостаточно прав для данного действия",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Группа с данным ID не была найдена",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @GetMapping("/all-by-task-{id}")
    public List<CommentDto> getAllCommentsByTaskId(@AuthenticationPrincipal UserDetails user,
                                                   @PathVariable("id") Long id) {
        return commentService.getCommentsByTask(user, id);
    }

    @Operation(summary = "Обновления комментария по ID")
    @ApiResponse(
            responseCode = "200",
            description = "Комментарий успешно обновлён",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = CommentDto.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Комментарий с данным ID не был найден",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @ApiResponse(
            responseCode = "403",
            description = "У пользователя недостаточно прав для данного действия",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Ошибка валидации данных",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @PutMapping("/{id}")
    public CommentDto updateCommentById(@AuthenticationPrincipal UserDetails user,
                                        @PathVariable("id") Long id,
                                        @RequestParam String text) {

        return commentService.updateCommentDto(user, id, text);
    }

    @Operation(summary = "Удаление комментария по ID")
    @ApiResponse(
            responseCode = "200",
            description = "Комментарий успешно удален",
            content = @Content(schema = @Schema(implementation = Void.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Комментарий с данным ID не был найден",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @ApiResponse(
            responseCode = "403",
            description = "У пользователя нет прав для данного действия",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @DeleteMapping("/{id}")
    public void deleteCommentById(@AuthenticationPrincipal UserDetails user,
                                  @PathVariable("id") Long id) {

        commentService.deleteCommentById(user, id);
    }
}
