package com.yauza.clothes.jwt_authentication_and_authorization;

import com.yauza.clothes.jwt_authentication_and_authorization.domain.JwtRequest;
import com.yauza.clothes.jwt_authentication_and_authorization.domain.JwtResponse;
import com.yauza.clothes.jwt_authentication_and_authorization.domain.Role;
import com.yauza.clothes.jwt_authentication_and_authorization.exception.AuthException;
import com.yauza.clothes.jwt_authentication_and_authorization.jwt_service.AuthenticationService;
import com.yauza.clothes.jwt_authentication_and_authorization.jwt_service.JwtProvider;
import io.jsonwebtoken.Claims;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;


@Controller
public class AuthenticationController {

    @Autowired
    private AuthenticationService authService;
    @Autowired
    private JwtProvider jwtProvider;

    private final int REFRESH_TOKEN_VALIDITY = 60 * 60 * 24 * 30; // 30 дней

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody JwtRequest authRequest, HttpServletResponse response) {
        final JwtResponse token;
        try {
            token = authService.login(authRequest);

            Cookie cookie = new Cookie("refreshToken", token.getRefreshToken());
            cookie.setPath("/");  // Доступ для всех путей
            cookie.setHttpOnly(true);  // Запрет доступа из JavaScript
            //        cookie.setSecure(true);  // Только для HTTPS
            cookie.setAttribute("SameSite", "Strict");
            cookie.setMaxAge(REFRESH_TOKEN_VALIDITY);
            response.addCookie(cookie);  // Браузер автоматически сохраняет cookie.

            String redirectEndpoint = sendTokenToSecureEndpoint(token);

            return ResponseEntity.ok()
                    .body(Map.of("redirectUrl", redirectEndpoint));
        } catch (AuthException e) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Ошибка входа. " + e.getMessage());
        }
    }


    @PostMapping("/token")
    public ResponseEntity<?> getNewAccessToken(HttpServletResponse response,
                                                    @CookieValue(value = "refreshToken") String refreshToken) {  // @CookieValue(value = "refreshToken", defaultValue = "")
        Date expirationDate = getExpirationDateFromRefreshToken(refreshToken);
        long daysLeft = daysUntilExpiration(expirationDate);
        JwtResponse token;

        try {
            if (daysLeft < 5) {
                token = authService.refresh(refreshToken);
                Cookie cookie = new Cookie("refreshToken", token.getRefreshToken());
                cookie.setPath("/");  // Доступ для всех путей
                cookie.setHttpOnly(true);  // Запрет доступа из JavaScript
                //        cookie.setSecure(true);  // Только для HTTPS
                cookie.setAttribute("SameSite", "Strict");
                cookie.setMaxAge(REFRESH_TOKEN_VALIDITY);
                response.addCookie(cookie);
            } else {
                // Если осталось больше 5 дней, получаем новый accessToken
                token = authService.getAccessToken(refreshToken);
            }

            sendTokenToSecureEndpoint(token);

            return ResponseEntity.ok()
                    .body("Токен доступа успешно обновлён");
        } catch (AuthException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Пройдите авторизацию повторно. " + e.getMessage());
        }
    }

    // Метод для создания HTTP-сущности и отправки GET-запроса на защищенный эндпоинт (при авторизации без хранения access-токена в JS).
    private String sendTokenToSecureEndpoint(JwtResponse token) {

        Claims claims = jwtProvider.getAccessClaims(token.getAccessToken());
        Set<String> roles = Arrays.stream(claims.get("roles").toString().split(","))
                .collect(Collectors.toSet());

        String redirectEndpoint;
        if (roles.contains(Role.ADMIN.getAuthority())) {
            redirectEndpoint = "/hello/admin";
        } else {
            redirectEndpoint = "/hello/user";
        }

        RestTemplate restTemplate = new RestTemplateBuilder()
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getAccessToken());
        HttpEntity<String> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<String> serviceResponse = restTemplate.exchange(
                    "http://localhost:8081" + redirectEndpoint,
                    HttpMethod.GET,
                    entity,
                    String.class
            );
            return redirectEndpoint;
        } catch (RestClientException e) {
            throw new AuthException("Ошибка при проверке токена: " + e.getMessage());
        }
    }

    // Метод для декодирования JWT и получения даты истечения
    private Date getExpirationDateFromRefreshToken(String refreshToken) {
        String[] parts = refreshToken.split("\\.");
        if (parts.length < 2) {
            throw new IllegalArgumentException("Invalid JWT token");
        }

        String payload = new String(Base64.getDecoder().decode(parts[1]));
        JSONObject jsonObject = new JSONObject(payload);
        long exp = jsonObject.getLong("exp");
        return new Date(exp * 1000);
    }

    private long daysUntilExpiration(Date expirationDate) {
        long diffInMillis = expirationDate.getTime() - System.currentTimeMillis();
        return TimeUnit.DAYS.convert(diffInMillis, TimeUnit.MILLISECONDS);
    }


    @PostMapping("/logout")
    public String logout(HttpServletResponse response,
                         @CookieValue(value = "refreshToken") String refreshToken) {
        authService.logout(refreshToken);
        Cookie cookie = new Cookie("refreshToken", "");
        cookie.setPath("/");
        cookie.setHttpOnly(true);
        cookie.setAttribute("SameSite", "Strict");
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:/auth";
    }

}
