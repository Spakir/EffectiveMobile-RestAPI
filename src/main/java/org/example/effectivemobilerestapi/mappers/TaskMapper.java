package org.example.effectivemobilerestapi.mappers;

import org.example.effectivemobilerestapi.dto.CommentDto;
import org.example.effectivemobilerestapi.dto.TaskDto;
import org.example.effectivemobilerestapi.entities.Comment;
import org.example.effectivemobilerestapi.entities.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = CommentMapper.class)
public interface TaskMapper {

    @Mapping(target = "author", source = "author.email")
    @Mapping(target = "executor", source = "executor.email")
    @Mapping(target = "comments", qualifiedByName = "mapCommentsToDto")
    TaskDto toTaskDto(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "executor", ignore = true)
    @Mapping(target = "comments", ignore = true)
    Task toTask(TaskDto taskDto);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "executor", ignore = true)
    @Mapping(target = "comments", ignore = true)
    void updateTaskFromDto(TaskDto taskDto, @MappingTarget Task task);

    @Named("mapCommentsToDto")
    default List<CommentDto> mapCommentsToDto(List<Comment> comments) {
        if (comments == null || comments.isEmpty()) {
            return null;
        }
        return comments.stream()
                .map(comment -> {
                    CommentDto dto = new CommentDto();
                    dto.setId(comment.getId());
                    dto.setText(comment.getText());
                    dto.setAuthor(comment.getAuthor().getEmail());
                    dto.setTaskId(comment.getTask().getId());
                    dto.setCreatedAt(comment.getCreatedAt());
                    return dto;
                })
                .collect(Collectors.toList());
    }
}