package org.example.effectivemobilerestapi.services.interfaces;

import org.example.effectivemobilerestapi.dto.TaskDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;

public interface TaskService {

    TaskDto saveTask(UserDetails user,TaskDto newTaskDto);

    TaskDto updateTask(UserDetails user,TaskDto updatedTaskDto);

    void deleteTask(Long id, UserDetails user);

    Page<TaskDto> getTasksByAuthor(UserDetails user, Pageable pageable);

    Page<TaskDto> getTasksByExecutor(UserDetails user, Pageable pageable);

    TaskDto changeExecutor(Long taskId, String executorEmail, UserDetails user);

    TaskDto changeTaskStatus(Long taskId, String status, UserDetails user);
}
