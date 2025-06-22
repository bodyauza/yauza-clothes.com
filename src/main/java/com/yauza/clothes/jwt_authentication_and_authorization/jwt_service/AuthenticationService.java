package com.yauza.clothes.jwt_authentication_and_authorization.jwt_service;

import com.yauza.clothes.jwt_authentication_and_authorization.domain.*;
import com.yauza.clothes.jwt_authentication_and_authorization.exception.AuthException;
import io.jsonwebtoken.Claims;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


@Service
@RequiredArgsConstructor
public class AuthenticationService {

    @Autowired
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtProvider jwtProvider;

    private final PasswordEncoder passwordEncoder;

    public JwtResponse login(@NonNull JwtRequest authRequest) {
        final User user = userRepository.findByLogin(authRequest.getLogin())
                .filter(u -> passwordEncoder.matches(authRequest.getPassword(), u.getPassword()))
                .orElseThrow(() -> new AuthException("Неверный логин или пароль"));
        refreshTokenRepository.deleteByUser(user);
        final String accessToken = jwtProvider.generateAccessToken(user);
        final String refreshToken = jwtProvider.generateRefreshToken(user);
        final Claims claims = jwtProvider.getRefreshClaims(refreshToken);
        RefreshToken refreshTokenEntity = new RefreshToken();
        refreshTokenEntity.setToken(refreshToken);
        refreshTokenEntity.setUser(user);
        refreshTokenEntity.setExpiryDate(claims.getExpiration());
        refreshTokenRepository.save(refreshTokenEntity);
        return new JwtResponse(accessToken, refreshToken);
    }

    public void logout(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            refreshTokenRepository.findByToken(refreshToken)
                    .ifPresent(refreshTokenRepository::delete);
            // Очищаем контекст безопасности для текущего пользователя. SecurityContextHolder хранит контекст в ThreadLocal по умолчанию.
            SecurityContextHolder.clearContext();
        } else {
            throw new AuthException("Невалидный JWT токен");
        }
    }

    public JwtResponse getAccessToken(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            RefreshToken savedRefreshToken = refreshTokenRepository.findByToken(refreshToken)
                    .orElseThrow(() -> new AuthException("Невалидный JWT токен"));
            final User user = savedRefreshToken.getUser();
            final String accessToken = jwtProvider.generateAccessToken(user);
            return new JwtResponse(accessToken, null);
        }
        throw new AuthException("Невалидный JWT токен");
    }

    public JwtResponse refresh(@NonNull String refreshToken) {
        if (jwtProvider.validateRefreshToken(refreshToken)) {
            RefreshToken savedRefreshToken = refreshTokenRepository.findByToken(refreshToken)
                    .orElseThrow(() -> new AuthException("Невалидный JWT токен"));
            final User user = savedRefreshToken.getUser();
            // Удаляем использованный refresh токен
            refreshTokenRepository.delete(savedRefreshToken);
            final String accessToken = jwtProvider.generateAccessToken(user);
            final String newRefreshToken = jwtProvider.generateRefreshToken(user);
            final Claims claims = jwtProvider.getRefreshClaims(newRefreshToken);
            // Сохраняем новый refresh токен
            RefreshToken refreshTokenEntity = new RefreshToken();
            refreshTokenEntity.setToken(newRefreshToken);
            refreshTokenEntity.setUser(user);
            refreshTokenEntity.setExpiryDate(claims.getExpiration());
            refreshTokenRepository.save(refreshTokenEntity);
            return new JwtResponse(accessToken, newRefreshToken);
        }
        throw new AuthException("Невалидный JWT токен");
    }

    public JwtAuthentication getAuthInfo() {
        return (JwtAuthentication) SecurityContextHolder.getContext().getAuthentication();
    }

}
