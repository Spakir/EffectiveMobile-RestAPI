package org.example.effectivemobilerestapi.services.impls;

import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.example.effectivemobilerestapi.enums.Role;
import org.example.effectivemobilerestapi.dto.UserDto;
import org.example.effectivemobilerestapi.entities.User;
import org.example.effectivemobilerestapi.mappers.UserMapper;
import org.example.effectivemobilerestapi.repositories.UserRepository;
import org.example.effectivemobilerestapi.services.interfaces.UserService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final UserMapper userMapper;

    @Override
    public UserDto saveUser(UserDto newUserDto) {
        if (existsByEmail(newUserDto.getEmail())) {
            throw new EntityExistsException("Данный email уже занят");
        }

        String defaultPassword = newUserDto.getPassword();
        newUserDto.setPassword(passwordEncoder.encode(defaultPassword));
        newUserDto.setRole(Role.USER);

        User newUserEntity = toUser(newUserDto);
        User savedUser = userRepository.save(newUserEntity);

        return toUserDto(savedUser);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("Пользователь не был найден"));

        return toUserDto(user);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    private User toUser(UserDto userDto) {
        return userMapper.toUser(userDto);
    }

    private UserDto toUserDto(User user) {
        return userMapper.toUserDto(user);
    }
}
