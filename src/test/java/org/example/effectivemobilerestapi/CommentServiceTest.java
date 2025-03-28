package org.example.effectivemobilerestapi;

import org.example.effectivemobilerestapi.dto.CommentDto;
import org.example.effectivemobilerestapi.dto.TaskDto;
import org.example.effectivemobilerestapi.entities.Comment;
import org.example.effectivemobilerestapi.entities.Task;
import org.example.effectivemobilerestapi.entities.User;
import org.example.effectivemobilerestapi.mappers.CommentMapper;
import org.example.effectivemobilerestapi.repositories.CommentRepository;
import org.example.effectivemobilerestapi.services.impls.CommentServiceImpl;
import org.example.effectivemobilerestapi.services.interfaces.TaskService;
import org.example.effectivemobilerestapi.services.interfaces.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;

import jakarta.persistence.EntityNotFoundException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CommentServiceTest {

    @Mock
    private CommentRepository commentRepository;

    @Mock
    private CommentMapper commentMapper;

    @Mock
    private UserService userService;

    @Mock
    private TaskService taskService;

    @InjectMocks
    private CommentServiceImpl commentService;

    private UserDetails userDetails;
    private UserDetails adminDetails;
    private CommentDto commentDto;
    private Comment comment;
    private User user;
    private Task task;

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

        user = new User();
        user.setId(1L);
        user.setEmail("user@example.com");

        task = new Task();
        task.setId(1L);
        task.setExecutor(user);

        commentDto = new CommentDto();
        commentDto.setId(1L);
        commentDto.setText("Test comment");
        commentDto.setTaskId(1L);
        commentDto.setAuthor("user@example.com");
        commentDto.setCreatedAt(LocalDateTime.now());

        comment = new Comment();
        comment.setId(1L);
        comment.setText("Test comment");
        comment.setTask(task);
        comment.setAuthor(user);
        comment.setCreatedAt(LocalDateTime.now());
    }

    @Test
    void saveComment_ShouldSaveCommentWhenUserIsExecutorOrAdmin() {
        // Arrange
        TaskDto taskDto = new TaskDto();
        taskDto.setExecutor("user@example.com");

        when(taskService.getTaskById(1L)).thenReturn(taskDto);
        when(commentMapper.toComment(any(CommentDto.class))).thenReturn(comment);
        when(userService.getUserEntityByEmail("user@example.com")).thenReturn(user);
        when(taskService.getTaskEntityById(1L)).thenReturn(task);
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toCommentDto(any(Comment.class))).thenReturn(commentDto);

        // Act
        CommentDto result = commentService.saveComment(userDetails, commentDto);

        // Assert
        assertNotNull(result);
        assertEquals("user@example.com", result.getAuthor());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void saveComment_ShouldThrowAccessDeniedWhenUserIsNotExecutorNorAdmin() {
        // Arrange
        TaskDto taskDto = new TaskDto();
        taskDto.setExecutor("other@example.com");

        when(taskService.getTaskById(1L)).thenReturn(taskDto);

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> commentService.saveComment(userDetails, commentDto));
    }

    @Test
    void getCommentById_ShouldReturnCommentWhenExists() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentMapper.toCommentDto(any(Comment.class))).thenReturn(commentDto);

        // Act
        CommentDto result = commentService.getCommentById(userDetails, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getCommentById_ShouldThrowExceptionWhenCommentNotFound() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> commentService.getCommentById(userDetails, 1L));
    }

    @Test
    void getCommentsByTask_ShouldReturnCommentsWhenUserHasAccess() {
        // Arrange
        TaskDto taskDto = new TaskDto();
        taskDto.setExecutor("user@example.com");
        taskDto.setAuthor("user@example.com");

        when(taskService.getTaskById(1L)).thenReturn(taskDto);
        when(commentRepository.findAllByTaskIdWithAuthor(1L)).thenReturn(List.of(comment));
        when(commentMapper.toCommentDto(any(Comment.class))).thenReturn(commentDto);

        // Act
        List<CommentDto> result = commentService.getCommentsByTask(userDetails, 1L);

        // Assert
        assertNotNull(result);
        assertEquals(1, result.size());
    }

    @Test
    void getCommentsByTask_ShouldThrowAccessDeniedWhenUserHasNoAccess() {
        // Arrange
        TaskDto taskDto = new TaskDto();
        taskDto.setExecutor("other@example.com");
        taskDto.setAuthor("another@example.com");

        when(taskService.getTaskById(1L)).thenReturn(taskDto);

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> commentService.getCommentsByTask(userDetails, 1L));
    }

    @Test
    void updateComment_ShouldUpdateCommentWhenUserIsAuthorOrAdmin() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));
        when(commentRepository.save(any(Comment.class))).thenReturn(comment);
        when(commentMapper.toCommentDto(any(Comment.class))).thenReturn(commentDto);

        // Act
        CommentDto result = commentService.updateCommentDto(userDetails, 1L, "Updated text");

        // Assert
        assertNotNull(result);
        assertEquals("Updated text", comment.getText());
        verify(commentRepository, times(1)).save(comment);
    }

    @Test
    void updateComment_ShouldThrowAccessDeniedWhenUserIsNotAuthorNorAdmin() {
        // Arrange
        UserDetails otherUser = org.springframework.security.core.userdetails.User.withUsername("other@example.com")
                .password("password")
                .authorities("USER")
                .build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> commentService.updateCommentDto(otherUser, 1L, "Updated text"));
    }

    @Test
    void deleteCommentById_ShouldDeleteCommentWhenUserIsAuthorOrAdmin() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        // Act
        commentService.deleteCommentById(userDetails, 1L);

        // Assert
        verify(commentRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteCommentById_ShouldThrowAccessDeniedWhenUserIsNotAuthorNorAdmin() {
        // Arrange
        UserDetails otherUser = org.springframework.security.core.userdetails.User.withUsername("other@example.com")
                .password("password")
                .authorities("USER")
                .build();

        when(commentRepository.findById(1L)).thenReturn(Optional.of(comment));

        // Act & Assert
        assertThrows(AccessDeniedException.class,
                () -> commentService.deleteCommentById(otherUser, 1L));
    }

    @Test
    void deleteCommentById_ShouldThrowExceptionWhenCommentNotFound() {
        // Arrange
        when(commentRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(EntityNotFoundException.class,
                () -> commentService.deleteCommentById(userDetails, 1L));
    }
}