package org.example.effectivemobilerestapi.services.interfaces;

import org.example.effectivemobilerestapi.dto.UserDto;
import org.example.effectivemobilerestapi.entities.User;

public interface UserService {

    UserDto saveUser(UserDto newUser);

    UserDto getUserByEmail(String email);

    boolean existsByEmail(String email);

    User getUserEntityByEmail(String email);
}
