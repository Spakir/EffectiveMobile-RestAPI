package org.example.effectivemobilerestapi.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.effectivemobilerestapi.dto.CommentDto;
import org.example.effectivemobilerestapi.services.interfaces.CommentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/comment")
public class CommentController {

    private final CommentService commentService;

    @PostMapping("/new")
    public CommentDto saveComment(@AuthenticationPrincipal UserDetails user,
                                  @Valid @RequestBody CommentDto commentDto) {

        return commentService.saveComment(user, commentDto);
    }

    @GetMapping("/{id}")
    public CommentDto getCommentById(@AuthenticationPrincipal UserDetails user,
                                     @PathVariable("id") Long id) {

        return commentService.getCommentById(user, id);
    }

    @GetMapping("/all-by-task-{id}")
    public List<CommentDto> getAllCommentsByTaskId(@AuthenticationPrincipal UserDetails user,
                                                   @PathVariable("id") Long id) {
        return commentService.getCommentsByTask(user, id);
    }

    @PutMapping("/{id}")
    public CommentDto updateCommentById(@AuthenticationPrincipal UserDetails user,
                                        @PathVariable("id") Long id,
                                        @RequestParam String text) {

        return commentService.updateCommentDto(user, id, text);
    }

    @DeleteMapping("/{id}")
    public void deleteCommentById(@AuthenticationPrincipal UserDetails user,
                                  @PathVariable("id") Long id) {

        commentService.deleteCommentById(user, id);
    }
}
