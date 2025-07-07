package com.yauza.clothes.jwt_authentication_and_authorization.domain;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
@Setter
public class RegistrationRequest {
    private String login;
    private String email;
    private String password;
    private String phone;
    private String firstName;
    private String lastName;
}
