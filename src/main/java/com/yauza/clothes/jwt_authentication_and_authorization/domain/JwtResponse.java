package com.yauza.clothes.jwt_authentication_and_authorization.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

// Ответ, возвращаемый пользователю.
@Getter
@AllArgsConstructor
public class JwtResponse {

    private final String type = "Bearer";
    private String accessToken;
    private String refreshToken;

}
