package com.yauza.clothes.jwt_authentication_and_authorization;

import com.yauza.clothes.jwt_authentication_and_authorization.domain.JwtRequest;
import com.yauza.clothes.jwt_authentication_and_authorization.domain.JwtResponse;
import com.yauza.clothes.jwt_authentication_and_authorization.domain.RefreshJwtRequest;
import com.yauza.clothes.jwt_authentication_and_authorization.jwt_service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@Controller
public class AuthenticationController {

    @Autowired
    private AuthenticationService authService;

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest authRequest) {
        final JwtResponse token = authService.login(authRequest);
        return ResponseEntity.ok(token);
    }

    @PostMapping("/token")
    public ResponseEntity<JwtResponse> getNewAccessToken(@RequestBody RefreshJwtRequest request) {
        final JwtResponse token = authService.getAccessToken(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

    // Только для аутентифицированных пользователей.
    // Необходим access-токен в заголовке запроса.
    @PostMapping("/refresh")
    public ResponseEntity<JwtResponse> getNewRefreshToken(@RequestBody RefreshJwtRequest request) {
        final JwtResponse token = authService.refresh(request.getRefreshToken());
        return ResponseEntity.ok(token);
    }

}
