package org.example.effectivemobilerestapi.mappers;

import org.example.effectivemobilerestapi.dto.TaskDto;
import org.example.effectivemobilerestapi.dto.UserDto;
import org.example.effectivemobilerestapi.entities.Task;
import org.example.effectivemobilerestapi.entities.User;
import org.mapstruct.*;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", uses = TaskMapper.class)
public interface UserMapper {

    @Mapping(target = "createdTasks", source = "createdTasks", qualifiedByName = "mapTasksToDto")
    @Mapping(target = "assignedTasks", source = "assignedTasks", qualifiedByName = "mapTasksToDto")
    UserDto toUserDto(User user);

    @Mapping(target = "createdTasks", ignore = true)
    @Mapping(target = "assignedTasks", ignore = true)
    User toUser(UserDto userDto);

    @Named("mapTasksToDto")
    default List<TaskDto> mapTasksToDto(List<Task> tasks) {
        if (tasks == null || tasks.isEmpty()) {
            return null;
        }
        return tasks.stream()
                .map(task -> {
                    TaskDto dto = new TaskDto();
                    dto.setId(task.getId());
                    dto.setHeader(task.getHeader());
                    dto.setDescription(task.getDescription());

                    return dto;
                })
                .collect(Collectors.toList());
    }

    default UserDetails toUserDetails(UserDto userDto) {
        return org.springframework.security.core.userdetails.User
                .withUsername(userDto.getEmail())
                .password(userDto.getPassword())
                .authorities(userDto.getRole().name())
                .build();
    }
}