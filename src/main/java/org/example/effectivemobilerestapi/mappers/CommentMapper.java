package org.example.effectivemobilerestapi.mappers;

import org.example.effectivemobilerestapi.dto.CommentDto;
import org.example.effectivemobilerestapi.entities.Comment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "author", source = "author.email")
    @Mapping(target = "taskId", source = "task.id")
    CommentDto toCommentDto(Comment comment);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "task", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    Comment toComment(CommentDto commentDto);
}