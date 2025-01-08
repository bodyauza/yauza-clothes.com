package com.yauza.clothes.jwt_authentication_and_authorization;

import com.yauza.clothes.jwt_authentication_and_authorization.domain.JwtRequest;
import com.yauza.clothes.jwt_authentication_and_authorization.domain.JwtResponse;
import com.yauza.clothes.jwt_authentication_and_authorization.domain.RefreshJwtRequest;
import com.yauza.clothes.jwt_authentication_and_authorization.jwt_service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;

@Controller
public class AuthenticationController {

    @Autowired
    private AuthenticationService authService;

    private final long REFRESH_TOKEN_VALIDITY = 60 * 60 * 24 * 30; // 30 дней

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody JwtRequest authRequest, HttpServletResponse response) {
        final JwtResponse token = authService.login(authRequest);
        Cookie cookie = new Cookie("refreshToken", token.getRefreshToken());
        cookie.setPath(";Path=/;HttpOnly;SameSite=strict;");
//        cookie.setSecure(true);
        cookie.setMaxAge((int) REFRESH_TOKEN_VALIDITY);
        response.addCookie(cookie);
        return ResponseEntity.ok(token.getAccessToken());
    }

    @PostMapping("/token")
    public ResponseEntity<JwtResponse> getNewAccessToken(@RequestBody RefreshJwtRequest request,
                                                         @CookieValue(value = "refreshToken", defaultValue = "") String refreshToken) {
        final JwtResponse token = authService.getAccessToken(refreshToken);
        return ResponseEntity.ok(token);
    }

    // Только для аутентифицированных пользователей.
    // Необходим access-токен в заголовке запроса.
    @PostMapping("/refresh")
    public ResponseEntity<String> getNewRefreshToken(HttpServletResponse response,
                                                     @CookieValue(value = "refreshToken", defaultValue = "") String refreshToken) {
        final JwtResponse token = authService.refresh(refreshToken);
        Cookie cookie = new Cookie("refreshToken", token.getRefreshToken());
        cookie.setPath(";Path=/;HttpOnly;SameSite=strict;");
//        cookie.setSecure(true);
        cookie.setMaxAge((int) REFRESH_TOKEN_VALIDITY);
        response.addCookie(cookie);
        return ResponseEntity.ok(token.getAccessToken());
    }

}
