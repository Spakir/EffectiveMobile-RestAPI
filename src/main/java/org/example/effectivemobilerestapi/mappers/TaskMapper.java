package org.example.effectivemobilerestapi.mappers;

import jakarta.persistence.EntityNotFoundException;
import org.example.effectivemobilerestapi.dto.CommentDto;
import org.example.effectivemobilerestapi.dto.TaskDto;
import org.example.effectivemobilerestapi.dto.UserDto;
import org.example.effectivemobilerestapi.entities.Comment;
import org.example.effectivemobilerestapi.entities.Task;
import org.example.effectivemobilerestapi.entities.User;
import org.example.effectivemobilerestapi.services.interfaces.UserService;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = CommentMapper.class)
public interface TaskMapper {

    @Mapping(target = "author", source = "author.email")
    @Mapping(target = "executor", source = "executor.email")
    @Mapping(target = "comments", qualifiedByName = "mapCommentsToDto")
    TaskDto toTaskDto(Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", source = "author", qualifiedByName = "mapUserFromEmail")
    @Mapping(target = "executor", source = "executor", qualifiedByName = "mapUserFromEmail")
    @Mapping(target = "comments", ignore = true)
    Task toTask(TaskDto taskDto, @Context UserService userService, @Context UserMapper userMapper);

    @Mapping(target = "id", ignore = true) // ID не обновляем
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "executor", ignore = true)
    void updateTaskFromDTO(TaskDto taskDto, @MappingTarget Task task);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "author", ignore = true)
    @Mapping(target = "executor", source = "executor", qualifiedByName = "mapUserFromEmail")
    @Mapping(target = "comments", ignore = true)
    void updateTaskFromDto(TaskDto taskDto,
                           @MappingTarget Task task,
                           @Context UserService userService,
                           @Context UserMapper userMapper);

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

    @Named("mapUserFromEmail")
    default User mapUserFromEmail(String email, @Context UserService userService, @Context UserMapper userMapper) {
        if (email == null) {
            return null;
        }

        UserDto userDto = userService.getUserByEmail(email);
        if (userDto == null) {
            throw new EntityNotFoundException("User with email " + email + " not found");
        }

        return userMapper.toUser(userDto);
    }
}