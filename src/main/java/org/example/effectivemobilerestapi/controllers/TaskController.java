package org.example.effectivemobilerestapi.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.effectivemobilerestapi.dto.ErrorResponseDto;
import org.example.effectivemobilerestapi.dto.TaskDto;
import org.example.effectivemobilerestapi.services.interfaces.TaskService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/task")
@Validated
@Tag(name = "Task API")
public class TaskController {

    private final TaskService taskService;

    @Operation(summary = "Создание новой задачи")
    @ApiResponse(
            responseCode = "200",
            description = "Задача успешно создана",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskDto.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Ошибка с валидацией входных данных",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @ApiResponse(
            responseCode = "403",
            description = "Ошибка авторизации",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @PostMapping("/new")
    public TaskDto saveTask(@AuthenticationPrincipal UserDetails user, @Valid @RequestBody TaskDto taskDto) {
        return taskService.saveTask(user, taskDto);
    }

    @Operation(summary = "Обновление задачи")
    @ApiResponse(
            responseCode = "200",
            description = "Задача успешно обновлена",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskDto.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Ошибка с валидацией входных данных",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @ApiResponse(
            responseCode = "403",
            description = "Недостаточно прав для действия",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @PutMapping("/{id}")
    public TaskDto updateTaskDto(@AuthenticationPrincipal UserDetails user,
                                 @PathVariable("id") Long id,
                                 @Valid @RequestBody TaskDto taskDto) {

        return taskService.updateTask(user, id, taskDto);
    }

    @Operation(summary = "Получение всех задач, созданных пользователем, который сделал запрос")
    @ApiResponse(
            responseCode = "200",
            description = "Получение всех задач",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskDto.class))
    )
    @GetMapping("/by-author")
    public Page<TaskDto> getTasksByAuthor(@AuthenticationPrincipal UserDetails user,
                                          @RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "10") int size,
                                          @RequestParam(defaultValue = "priority") String sort,
                                          @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        return taskService.getTasksByAuthor(user, pageable);
    }

    @Operation(summary = "Получение всех задач, для которых пользователь является исполнителем")
    @ApiResponse(
            responseCode = "200",
            description = "Успешное получение всех задач",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskDto.class))
    )
    @GetMapping("/by-executor")
    public Page<TaskDto> getTasksByExecutor(@AuthenticationPrincipal UserDetails user,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size,
                                            @RequestParam(defaultValue = "priority") String sort,
                                            @RequestParam(defaultValue = "desc") String direction) {
        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sort));

        return taskService.getTasksByExecutor(user, pageable);
    }

    @Operation(summary = "Удаление задачи по ID")
    @ApiResponse(
            responseCode = "200",
            description = "Успешное удаление задачи",
            content = @Content(schema = @Schema(implementation = Void.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Задача с данным ID не была найдена",
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
    public void deleteTask(@AuthenticationPrincipal UserDetails user,
                           @PathVariable("id") Long id) {

        taskService.deleteTask(id, user);
    }

    @Operation(summary = "Получение задачи по ID")
    @ApiResponse(
            responseCode = "200",
            description = "Задача успешно найдена",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Задача с данным ID не была найдена",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @ApiResponse(
            responseCode = "403",
            description = "У пользователя нет прав для данного действия",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @GetMapping("/{id}")
    public TaskDto getTaskById(@AuthenticationPrincipal UserDetails user,
                               @PathVariable("id") Long id) {

        return taskService.getTaskById(id);
    }

    @Operation(summary = "Обновление исполнителя задачи")
    @ApiResponse(
            responseCode = "200",
            description = "Исполнитель задачи успешно обновлён",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = TaskDto.class))
    )
    @ApiResponse(
            responseCode = "404",
            description = "Задача с данным ID не была найдена",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @ApiResponse(
            responseCode = "403",
            description = "У пользователя нет прав для данного действия",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @ApiResponse(
            responseCode = "500",
            description = "Ошибка валидации данных",
            content = @Content(mediaType = "application/json",
                    schema = @Schema(implementation = ErrorResponseDto.class))
    )
    @PutMapping("update-executor-{id}")
    public TaskDto changeExecutor(@AuthenticationPrincipal UserDetails user,
                                  @PathVariable("id") Long id,
                                  @RequestParam String executorName) {

        return taskService.changeExecutor(user, id, executorName);
    }
}
