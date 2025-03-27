package org.example.effectivemobilerestapi.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
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
public class TaskController {

    private final TaskService taskService;

    @PostMapping("/new")
    public TaskDto saveTask(@AuthenticationPrincipal UserDetails user, @Valid @RequestBody TaskDto taskDto) {
        return taskService.saveTask(user, taskDto);
    }

    @PutMapping("/{id}")
    public TaskDto updateTaskDto(@AuthenticationPrincipal UserDetails user,
                                 @PathVariable("id") Long id,
                                 @Valid @RequestBody TaskDto taskDto) {
        return taskService.updateTask(user, id, taskDto);
    }

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

    @DeleteMapping("/{id}")
    public void deleteTask(@AuthenticationPrincipal UserDetails user,
                           @PathVariable("id") Long id) {

        taskService.deleteTask(id, user);
    }

    @GetMapping("/{id}")
    public TaskDto getTaskById(@AuthenticationPrincipal UserDetails user,
                               @PathVariable("id") Long id) {

        return taskService.getTaskById(id);
    }

    @PutMapping("update-executor-{id}")
    public TaskDto changeExecutor(@AuthenticationPrincipal UserDetails user,
                                  @PathVariable("id") Long id,
                                  @RequestParam String executorName) {

        return taskService.changeExecutor(user, id, executorName);
    }
}
