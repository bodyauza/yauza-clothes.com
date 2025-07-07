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

    public static final String LOGIN_PATTERN = "^[a-zA-Z0-9_-]{3,20}$";
    public static final String EMAIL_PATTERN = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
    public static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z]).{8,}$";
    public static final String PHONE_PATTERN = "^\\+?[0-9]{10,15}$";
    public static final String NAME_PATTERN = "^[A-Za-zА-Яа-яЁё\\- ]{2,50}$";

    public User registration(String login, String email, String password,
                             String phone, String firstName, String lastName) {

        // Проверка на null и пустые строки
        if (login == null || login.isBlank() ||
                email == null || email.isBlank() ||
                password == null || password.isBlank()) {
            throw new IllegalArgumentException("Все обязательные поля должны быть заполнены");
        }

        // Валидация с помощью регулярных выражений
        validateField(login, LOGIN_PATTERN, "Некорректный логин");
        validateField(email, EMAIL_PATTERN, "Некорректный email");
        validateField(password, PASSWORD_PATTERN, "Пароль должен содержать минимум 8 символов, цифры, заглавные и строчные буквы");

        if (phone != null && !phone.isBlank()) {
            validateField(phone, PHONE_PATTERN, "Некорректный номер телефона");
        }

        if (firstName != null && !firstName.isBlank()) {
            validateField(firstName, NAME_PATTERN, "Некорректное имя");
        }

        if (lastName != null && !lastName.isBlank()) {
            validateField(lastName, NAME_PATTERN, "Некорректная фамилия");
        }

        if (userRepository.existsByLoginOrEmail(login, email)) {
            throw new IllegalArgumentException("Пользователь с таким логином или email уже существует");
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

    private void validateField(String value, String pattern, String errorMessage) {
        if (!value.matches(pattern)) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
