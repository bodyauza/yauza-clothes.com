package com.yauza.clothes.jwt_authentication_and_authorization.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;
import java.util.Set;

/*

    Реализуя интерфейс Authentication, JwtAuthentication становится совместимым с SecurityContext,
    что позволяет Spring Security автоматически проверять, аутентифицирован ли пользователь и имеет ли он
    соответствующие права для доступа к защищенным ресурсам.

    getCredentials() и getDetails() возвращают null, так как они не нужны для работы с JWT.
    В JWT-аутентификации нет пароля на каждом запросе, а используется токен, который уже содержит всю
    необходимую информацию.

*/

@Getter
@Setter
public class JwtAuthentication implements Authentication {

    private boolean authenticated;
    private String username;
    private String firstName;
    private Set<Role> roles;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles;
    }

    @Override
    public Object getCredentials() {
        return null;
    }

    @Override
    public Object getDetails() {
        return null;
    }

    @Override
    public Object getPrincipal() {
        return username;
    }

    @Override
    public boolean isAuthenticated() {
        return authenticated;
    }

    @Override
    public void setAuthenticated(boolean isAuthenticated) throws IllegalArgumentException {
        this.authenticated = isAuthenticated;
    }

    @Override
    public String getName() {
        return firstName;
    }

}
