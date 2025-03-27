package org.example.effectivemobilerestapi.services.impls;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.effectivemobilerestapi.dto.CommentDto;
import org.example.effectivemobilerestapi.dto.TaskDto;
import org.example.effectivemobilerestapi.entities.Comment;
import org.example.effectivemobilerestapi.mappers.CommentMapper;
import org.example.effectivemobilerestapi.repositories.CommentRepository;
import org.example.effectivemobilerestapi.services.interfaces.CommentService;
import org.example.effectivemobilerestapi.services.interfaces.TaskService;
import org.example.effectivemobilerestapi.services.interfaces.UserService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;

    private final CommentMapper commentMapper;

    private final UserService userService;

    private final TaskService taskService;

    @Override
    @Transactional
    public CommentDto saveComment(UserDetails user, CommentDto newCommentDto) {
        String taskExecutorUsername = taskService
                .getTaskById(newCommentDto.getTaskId())
                .getExecutor();

        System.out.println(isAdmin(user) || user.getUsername().equals(taskExecutorUsername));

        if (!isAdmin(user) && !user.getUsername().equals(taskExecutorUsername)) {
            throw new AccessDeniedException("У вас нет права оставлять комментарии к этой задаче");
        }

        newCommentDto.setAuthor(user.getUsername());

        Comment comment = commentMapper.toComment(newCommentDto);

        comment.setAuthor(userService.getUserEntityByEmail(user.getUsername()));
        comment.setTask(taskService.getTaskEntityById(newCommentDto.getTaskId()));

        Comment savedComment = commentRepository.save(comment);

        return commentMapper.toCommentDto(savedComment);
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getCommentById(UserDetails user, Long id) {

        return commentMapper.toCommentDto(commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Комментария с данным Id не обнаружено")));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByTask(UserDetails user, Long taskId) {
        TaskDto task = taskService.getTaskById(taskId);
        if (!isAdmin(user)
                && !user.getUsername().equals(task.getExecutor())
                && !user.getUsername().equals(task.getAuthor())) {
            throw new AccessDeniedException("У вас нет прав на эту команду");
        }

        return commentRepository.findAllByTaskIdWithAuthor(taskId)
                .stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public CommentDto updateCommentDto(UserDetails user, Long id, String text) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий с данным ID не был найден"));

        if (!isAdmin(user) && !user.getUsername().equals(comment.getAuthor().getEmail())){
            throw new AccessDeniedException("У вас нет прав на эту команду");
        }

        comment.setText(text);
        Comment updatedComment = commentRepository.save(comment);

        return commentMapper.toCommentDto(updatedComment);
    }

    @Transactional
    @Override
    public void deleteCommentById(UserDetails user, Long id) {
        Comment comment = commentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Комментарий с данным ID не найден"));

        if (!isAdmin(user) && !comment.getAuthor().getEmail().equals(user.getUsername())) {
            throw new AccessDeniedException("У вас нет прав на эту команду");
        }

        commentRepository.deleteById(id);
    }

    private boolean isAdmin(UserDetails user) {
        return user.getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals("ADMIN"));
    }
}
