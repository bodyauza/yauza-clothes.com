package com.yauza.clothes.jwt_authentication_and_authorization.domain;

import lombok.Getter;
import lombok.Setter;

// Запрос, который пользователь отправляет для получения JWT-токена.
@Setter
@Getter
public class JwtRequest {

    private String login;
    private String password;

}
