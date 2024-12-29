package com.yauza.clothes.jwt_authentication_and_authorization.domain;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RefreshJwtRequest {

    public String refreshToken;

}
