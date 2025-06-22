package com.yauza.clothes.jwt_authentication_and_authorization.jwt_service;

import com.yauza.clothes.jwt_authentication_and_authorization.domain.Role;
import com.yauza.clothes.jwt_authentication_and_authorization.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public User registration(String login, String email, String password,
                             String phone, String firstName, String lastName) {

        if (login == null || login.isBlank() ||
                email == null || email.isBlank() ||
                password == null || password.isBlank()) {
            throw new IllegalArgumentException("Invalid input data");
        }

        if (userRepository.existsByLoginOrEmail(login, email)) {
            throw new IllegalArgumentException("User already exists");
        }

        User user = new User();
        user.setLogin(login);
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));
        user.setPhone(phone);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setRoles(Collections.singleton(Role.USER));

        return userRepository.save(user);
    }
}
