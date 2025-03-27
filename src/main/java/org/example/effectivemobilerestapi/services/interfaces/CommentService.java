package org.example.effectivemobilerestapi.services.interfaces;

import org.example.effectivemobilerestapi.dto.CommentDto;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.List;

public interface CommentService {

    CommentDto saveComment(UserDetails user, CommentDto newComment);

    CommentDto getCommentById(UserDetails user, Long id);

    List<CommentDto> getCommentsByTask(UserDetails user, Long taskId);

    CommentDto updateCommentDto(UserDetails user, Long id, String text);

    void deleteCommentById(UserDetails user, Long Id);
}
