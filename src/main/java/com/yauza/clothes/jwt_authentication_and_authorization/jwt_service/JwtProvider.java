package com.yauza.clothes.jwt_authentication_and_authorization.jwt_service;

import com.yauza.clothes.jwt_authentication_and_authorization.domain.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

/*

    Один ключ используется для генерации access-токенов, а второй — для refresh-токенов.
    Это позволяет отделить сервисы с бизнес-логикой, которые смогут валидировать access-токены,
    но не будут иметь доступа к refresh-токенам. Если такой сервис будет скомпрометирован,
    можно будет просто заменить ключ access-токена, не затрагивая refresh-токены и не разлогинивая
    всех пользователей.

    С помощью подписи с использованием секретного ключа веб-приложение проверяет, что токен
    действительно был сгенерирован им.

 */

@Slf4j
@Component
public class JwtProvider {

    private final SecretKey jwtAccessSecret;
    private final SecretKey jwtRefreshSecret;

    public JwtProvider(
            @Value("${jwt.secret.access}") String jwtAccessSecret,
            @Value("${jwt.secret.refresh}") String jwtRefreshSecret
    ) {
        this.jwtAccessSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtAccessSecret));
        this.jwtRefreshSecret = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtRefreshSecret));
    }

    public String generateAccessToken(@NonNull User user) {
        // Получаем текущее время
        final LocalDateTime now = LocalDateTime.now();

        // Вычисляем момент истечения токена, добавляя 5 минут к текущему времени
        final Instant accessExpirationInstant = now.plusMinutes(5).atZone(ZoneId.systemDefault()).toInstant();

        // Преобразуем Instant в объект Date, который используется в JWT
        final Date accessExpiration = Date.from(accessExpirationInstant);

        // Создаем JWT-токен с использованием билдера
        return Jwts.builder()
                // Устанавливаем субъект токена
                .setSubject(user.getLogin())
                // Устанавливаем время истечения токена
                .setExpiration(accessExpiration)
                // Подписываем токен с использованием секретного ключа для доступа
                .signWith(jwtAccessSecret)
                // Добавляем дополнительные данные (claims) в токен
                .claim("roles", user.getRoles())
                .claim("firstName", user.getFirstName())
                // Завершаем создание токена и возвращаем его как строку
                .compact();
    }

    public String generateRefreshToken(@NonNull User user) {
        final LocalDateTime now = LocalDateTime.now();
        final Instant refreshExpirationInstant = now.plusDays(30).atZone(ZoneId.systemDefault()).toInstant();
        final Date refreshExpiration = Date.from(refreshExpirationInstant);
        return Jwts.builder()
                .setSubject(user.getLogin())
                .setExpiration(refreshExpiration)
                .signWith(jwtRefreshSecret)
                .compact();
    }

    public boolean validateAccessToken(@NonNull String accessToken) {
        return validateToken(accessToken, jwtAccessSecret);
    }

    public boolean validateRefreshToken(@NonNull String refreshToken) {
        return validateToken(refreshToken, jwtRefreshSecret);
    }

    private boolean validateToken(@NonNull String token, @NonNull Key secret) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(secret)
                    // Создает парсер JWT и устанавливает секретный ключ для подписи.
                    .build()
                    .parseClaimsJws(token);
            // Парсит токен, проверяя его подпись и извлекая утверждения (claims).
            return true;
            // Если парсинг прошел успешно, возвращает true (токен валиден).
        } catch (ExpiredJwtException expEx) {
            // Обрабатывает исключение, если токен истек.
            log.error("Token expired", expEx);
            // Логирует ошибку с сообщением о том, что токен истек.
        } catch (UnsupportedJwtException unsEx) {
            // Обрабатывает исключение, если токен не поддерживается.
            log.error("Unsupported jwt", unsEx);
            // Логирует ошибку с сообщением о неподдерживаемом токене.
        } catch (MalformedJwtException mjEx) {
            // Обрабатывает исключение, если токен имеет неправильный формат.
            log.error("Malformed jwt", mjEx);
            // Логирует ошибку с сообщением о неправильно сформированном токене.
        } catch (SignatureException sEx) {
            // Обрабатывает исключение, если подпись токена недействительна.
            log.error("Invalid signature", sEx);
            // Логирует ошибку с сообщением о недействительной подписи.
        } catch (Exception e) {
            // Обрабатывает любые другие исключения, которые могут возникнуть.
            log.error("invalid token", e);
            // Логирует ошибку с сообщением о недействительном токене.
        }
        return false;
        // Если произошла ошибка, возвращает false (токен недействителен).
    }

    public Claims getAccessClaims(@NonNull String token) {
        return getClaims(token, jwtAccessSecret);
    }

    public Claims getRefreshClaims(@NonNull String token) {
        return getClaims(token, jwtRefreshSecret);
    }

    private Claims getClaims(@NonNull String token, @NonNull Key secret) {
        return Jwts.parserBuilder()
                .setSigningKey(secret)
                // Создает парсер JWT и устанавливает секретный ключ для подписи.
                .build()
                .parseClaimsJws(token)
                .getBody();
        // Парсит токен и возвращает его тело (утверждения).
    }

}