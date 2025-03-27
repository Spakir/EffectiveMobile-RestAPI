package org.example.effectivemobilerestapi.services.interfaces;

import org.example.effectivemobilerestapi.dto.TaskDto;
import org.example.effectivemobilerestapi.entities.Task;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

public interface TaskService {

    TaskDto saveTask(UserDetails user, TaskDto newTaskDto);

    TaskDto updateTask(UserDetails user, Long id, TaskDto updatedTaskDto);

    Task getTaskEntityById(Long id);

    void deleteTask(Long id, UserDetails user);

    TaskDto getTaskById(Long id);

    Page<TaskDto> getTasksByAuthor(UserDetails user, Pageable pageable);

    Page<TaskDto> getTasksByExecutor(UserDetails user, Pageable pageable);

    TaskDto changeExecutor(UserDetails user, Long taskId, String executorName);
}
