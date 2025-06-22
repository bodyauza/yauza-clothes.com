package com.yauza.clothes.jwt_authentication_and_authorization.jwt_service;

import com.yauza.clothes.jwt_authentication_and_authorization.domain.User;
import com.yauza.clothes.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(String login);

    boolean existsByLoginOrEmail(String login, String email);
}
