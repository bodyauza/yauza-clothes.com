package com.yauza.clothes.jwt_authentication_and_authorization.jwt_service;

import com.yauza.clothes.jwt_authentication_and_authorization.domain.RefreshToken;
import com.yauza.clothes.jwt_authentication_and_authorization.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    void deleteByUser(User user);

    void deleteByToken(String token);

    List<RefreshToken> findByUser(User user);
}
