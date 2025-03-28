package org.example.effectivemobilerestapi;

import jakarta.persistence.EntityNotFoundException;
import org.example.effectivemobilerestapi.dto.TaskDto;
import org.example.effectivemobilerestapi.entities.Task;
import org.example.effectivemobilerestapi.entities.User;
import org.example.effectivemobilerestapi.enums.Priority;
import org.example.effectivemobilerestapi.enums.Role;
import org.example.effectivemobilerestapi.enums.Status;
import org.example.effectivemobilerestapi.mappers.TaskMapper;
import org.example.effectivemobilerestapi.repositories.TaskRepository;
import org.example.effectivemobilerestapi.services.impls.TaskServiceImpl;
import org.example.effectivemobilerestapi.services.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepository taskRepository;

    @Mock
    private TaskMapper taskMapper;

    @Mock
    private UserService userService;

    @InjectMocks
    private TaskServiceImpl taskService;

    private UserDetails userDetails;
    private UserDetails adminDetails;
    private TaskDto taskDto;
    private Task task;
    private User user;

    @BeforeEach
    void setUp() {
        userDetails = org.springframework.security.core.userdetails.User.withUsername("user@example.com")
                .password("password")
                .authorities("USER")
                .build();

        adminDetails = org.springframework.security.core.userdetails.User.withUsername("admin@example.com")
                .password("password")
                .authorities("ADMIN")
                .build();

        taskDto = new TaskDto();
        taskDto.setId(1L);
        taskDto.setHeader("Test Header");
        taskDto.setDescription("Test Description");
        taskDto.setStatus(Status.PROCESSING);
        taskDto.setPriority(Priority.MEDIUM);
        taskDto.setAuthor("user@example.com");
        taskDto.setExecutor("user@example.com");

        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");
        user.setPassword("password");
        user.setRole(Role.USER);

        task = new Task();
        task.setId(1L);
        task.setHeader("Test Header");
        task.setDescription("Test Description");
        task.setStatus(Status.PROCESSING);
        task.setPriority(Priority.MEDIUM);
        task.setAuthor(user);
        task.setExecutor(user);
    }

    @Test
    void saveTask_ShouldSaveTaskWithCurrentUserAsAuthorAndExecutor() {
        when(taskMapper.toTask(any(TaskDto.class), any(), any())).thenReturn(task);
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toTaskDto(any(Task.class))).thenReturn(taskDto);

        TaskDto result = taskService.saveTask(userDetails, taskDto);

        assertNotNull(result);
        assertEquals("user@example.com", result.getAuthor());
        assertEquals("user@example.com", result.getExecutor());
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void updateTask_ShouldUpdateTaskWhenUserIsExecutor() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toTaskDto(any(Task.class))).thenReturn(taskDto);

        TaskDto result = taskService.updateTask(userDetails, 1L, taskDto);

        assertNotNull(result);
        verify(taskMapper, times(1)).updateTaskFromDTO(taskDto, task);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void updateTask_ShouldThrowAccessDeniedWhenUserIsNotExecutorNorAdmin() {
        UserDetails otherUser = org.springframework.security.core.userdetails.User.withUsername("other@example.com")
                .password("password")
                .authorities("USER")
                .build();

        assertThrows(AccessDeniedException.class,
                () -> taskService.updateTask(otherUser, 1L, taskDto));
    }

    @Test
    void updateTask_ShouldAllowAdminToUpdateAnyTask() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskRepository.save(any(Task.class))).thenReturn(task);
        when(taskMapper.toTaskDto(any(Task.class))).thenReturn(taskDto);

        TaskDto result = taskService.updateTask(adminDetails, 1L, taskDto);

        assertNotNull(result);
        verify(taskRepository, times(1)).save(task);
    }

    @Test
    void deleteTask_ShouldDeleteTaskWhenUserIsExecutor() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toTaskDto(any(Task.class))).thenReturn(taskDto);

        taskService.deleteTask(1L, userDetails);

        verify(taskRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteTask_ShouldThrowAccessDeniedWhenUserIsNotExecutorNorAdmin() {
        UserDetails otherUser = org.springframework.security.core.userdetails.User.withUsername("other@example.com")
                .password("password")
                .authorities("USER")
                .build();

        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toTaskDto(any(Task.class))).thenReturn(taskDto);

        assertThrows(AccessDeniedException.class,
                () -> taskService.deleteTask(1L, otherUser));
    }

    @Test
    void getTaskById_ShouldReturnTaskWhenExists() {
        when(taskRepository.findById(1L)).thenReturn(Optional.of(task));
        when(taskMapper.toTaskDto(any(Task.class))).thenReturn(taskDto);

        TaskDto result = taskService.getTaskById(1L);

        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getTaskById_ShouldThrowExceptionWhenTaskNotFound() {
        when(taskRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class,
                () -> taskService.getTaskById(1L));
    }

    @Test
    void getTasksByAuthor_ShouldReturnTasksForAuthor() {
        Pageable pageable = Pageable.unpaged();
        Page<Task> taskPage = new PageImpl<>(List.of(task));

        when(taskRepository.findByAuthorEmail("user@example.com", pageable))
                .thenReturn(taskPage);
        when(taskMapper.toTaskDto(any(Task.class))).thenReturn(taskDto);

        Page<TaskDto> result = taskService.getTasksByAuthor(userDetails, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void getTasksByExecutor_ShouldReturnTasksForExecutor() {
        Pageable pageable = Pageable.unpaged();
        Page<Task> taskPage = new PageImpl<>(List.of(task));

        when(taskRepository.findByExecutorEmail("user@example.com", pageable))
                .thenReturn(taskPage);
        when(taskMapper.toTaskDto(any(Task.class))).thenReturn(taskDto);

        Page<TaskDto> result = taskService.getTasksByExecutor(userDetails, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void changeExecutor_ShouldThrowAccessDeniedForNonAdmin() {
        assertThrows(AccessDeniedException.class,
                () -> taskService.changeExecutor(userDetails, 1L, "new@example.com"));
    }

    @Test
    void changeExecutor_ShouldThrowExceptionWhenExecutorNotFound() {
        when(userService.existsByEmail("nonexistent@example.com")).thenReturn(false);

        assertThrows(EntityNotFoundException.class,
                () -> taskService.changeExecutor(adminDetails, 1L, "nonexistent@example.com"));
    }
}