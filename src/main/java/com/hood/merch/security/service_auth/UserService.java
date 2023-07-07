package com.hood.merch.security.service_auth;

import com.hood.merch.security.domain.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final List<User> users;
    private User user;

    public UserService() {
        this.users = List.of(
                new User(user.getLogin(), user.getPassword(), user.getFirstName(), user.getLastName(), user.getRole())
        );
        //new User(user.getLogin(), user.getPassword(), user.getFirstName(), user.getLastName(), Collections.singleton(Role.USER))
    }

    public Optional<User> getByLogin(@NonNull String login) {
        return users.stream()
                .filter(user -> login.equals(user.getLogin()))
                .findFirst();
    }

}