package org.example.effectivemobilerestapi.services.interfaces;

import org.example.effectivemobilerestapi.dto.TaskDto;
import org.example.effectivemobilerestapi.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.file.AccessDeniedException;
import java.util.Optional;

public interface TaskService {

    TaskDto saveTask(UserDetails user, TaskDto newTaskDto);

    TaskDto updateTask(UserDetails user, Long id, TaskDto updatedTaskDto) throws AccessDeniedException;

    void deleteTask(Long id, UserDetails user) throws AccessDeniedException;

    TaskDto getTaskById(Long id);

    Page<TaskDto> getTasksByAuthor(UserDetails user, Pageable pageable);

    Page<TaskDto> getTasksByExecutor(UserDetails user, Pageable pageable);
}
