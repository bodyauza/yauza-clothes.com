package com.hood.merch.security.service_auth;

import com.hood.merch.security.model_auth.User;
import com.hood.merch.security.repo_auth.UserRepository;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UserService {

    @Autowired
    private List<User> users;
    @Autowired
    private UserRepository userRepository;

    public UserService() {
        this.users = userRepository.findAll();
    }

    public Optional<User> getByLogin(@NonNull String login) {
        return users.stream()
                .filter(user -> login.equals(user.getLogin()))
                .findFirst();
    }

}