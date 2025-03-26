package org.example.effectivemobilerestapi.services.impls;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.effectivemobilerestapi.dto.TaskDto;
import org.example.effectivemobilerestapi.entities.Task;
import org.example.effectivemobilerestapi.mappers.TaskMapper;
import org.example.effectivemobilerestapi.mappers.UserMapper;
import org.example.effectivemobilerestapi.repositories.TaskRepository;
import org.example.effectivemobilerestapi.services.interfaces.TaskService;
import org.example.effectivemobilerestapi.services.interfaces.UserService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.file.AccessDeniedException;

@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final TaskMapper taskMapper;

    private final TaskRepository taskRepository;

    private final UserService userService;

    private final UserMapper userMapper;

    @Override
    @Transactional
    public TaskDto saveTask(UserDetails user, TaskDto newTaskDto) {
        newTaskDto.setAuthor(user.getUsername());

        if (newTaskDto.getExecutor() == null) {
            newTaskDto.setExecutor(user.getUsername());
        }

        Task newTask = taskMapper.toTask(newTaskDto, userService, userMapper);
        Task createdTask = taskRepository.save(newTask);

        return taskMapper.toTaskDto(createdTask);
    }

    @Override
    @Transactional
    public TaskDto updateTask(UserDetails user, Long id, TaskDto taskDto) throws AccessDeniedException {
        if (!isAdmin(user) && (taskDto.getExecutor() == null || !taskDto.getExecutor().equals(user.getUsername()))) {
            throw new AccessDeniedException("У вас нет прав изменять эту задачу");
        }

        Task existingTask = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Задача с данным ID не была найдена"));

        taskMapper.updateTaskFromDTO(taskDto, existingTask);
        Task updatedTask = taskRepository.save(existingTask);

        return taskMapper.toTaskDto(updatedTask);
    }

    @Override
    public void deleteTask(Long id, UserDetails user) throws AccessDeniedException {
        if (!isAdmin(user) && !user.getUsername().equals(getTaskById(id).getExecutor())) {
            throw new AccessDeniedException("У вас нет прав изменять эту задачу");
        }

        taskRepository.deleteById(id);
    }

    @Override
    public TaskDto getTaskById(Long id) {
        Task existTask = taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Задачи с данной ID не существует"));

        return taskMapper.toTaskDto(existTask);
    }

    @Override
    public Page<TaskDto> getTasksByAuthor(UserDetails user, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByAuthorEmail(user.getUsername(), pageable);
        return tasks.map(taskMapper::toTaskDto);
    }

    @Override
    public Page<TaskDto> getTasksByExecutor(UserDetails user, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByExecutorEmail(user.getUsername(), pageable);
        return tasks.map(taskMapper::toTaskDto);
    }

    private boolean isAdmin(UserDetails user) {
        return user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"));
    }
}
