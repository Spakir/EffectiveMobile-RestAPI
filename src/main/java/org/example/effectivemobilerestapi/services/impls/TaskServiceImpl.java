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
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public TaskDto updateTask(UserDetails user, Long id, TaskDto taskDto) {
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
    public Task getTaskEntityById(Long id) {
        return getExistTask(id);
    }

    @Override
    @Transactional
    public void deleteTask(Long id, UserDetails user){
        if (!isAdmin(user) && !user.getUsername().equals(getTaskById(id).getExecutor())) {
            throw new AccessDeniedException("У вас нет прав изменять эту задачу");
        }

        taskRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public TaskDto getTaskById(Long id) {
        Task existTask = getExistTask(id);

        return taskMapper.toTaskDto(existTask);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskDto> getTasksByAuthor(UserDetails user, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByAuthorEmail(user.getUsername(), pageable);
        return tasks.map(taskMapper::toTaskDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<TaskDto> getTasksByExecutor(UserDetails user, Pageable pageable) {
        Page<Task> tasks = taskRepository.findByExecutorEmail(user.getUsername(), pageable);
        return tasks.map(taskMapper::toTaskDto);
    }

    @Override
    @Transactional
    public TaskDto changeExecutor(UserDetails user, Long taskId, String executorName) {
        if(!isAdmin(user)){
            throw new AccessDeniedException("У вас нет прав изменять исполнителя задачи");
        }

        if(!userService.existsByEmail(executorName)){
            throw new EntityNotFoundException("Юзера с таким email не существует");
        }

        Task task = getExistTask(taskId);
        task.setExecutor(userMapper.toUser(userService.getUserByEmail(executorName)));
        Task savedTask = taskRepository.save(task);

        return taskMapper.toTaskDto(savedTask);
    }

    private boolean isAdmin(UserDetails user) {
        return user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
    }

    private Task getExistTask(Long id){
        return taskRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Задачи с данной ID не существует"));
    }
}
