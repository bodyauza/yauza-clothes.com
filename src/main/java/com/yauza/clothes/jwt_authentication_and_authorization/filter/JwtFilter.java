package com.yauza.clothes.jwt_authentication_and_authorization.filter;

import com.yauza.clothes.jwt_authentication_and_authorization.domain.JwtAuthentication;
import com.yauza.clothes.jwt_authentication_and_authorization.jwt_service.JwtProvider;
import com.yauza.clothes.jwt_authentication_and_authorization.jwt_service.JwtUtils;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.GenericFilterBean;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;

/*

    1.) Извлекается значение заголовка Authorization из HTTP-запроса.
    2.) Проверяется, что токен не равен null и что он является валидным.
    3.) Если токен прошел проверку, извлекаются его claims, и на их основе создается объект аутентификации
        JwtAuthentication с помощью метода JwtUtils.generate.
    4.) После успешной аутентификации объект JwtAuthentication помещается в контекст безопасности
        (SecurityContextHolder), что позволяет другим частям приложения получать информацию о текущем пользователе.

*/

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtFilter extends GenericFilterBean {

    private static final String AUTHORIZATION = "Authorization";

    private final JwtProvider jwtProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain fc)
            throws IOException, ServletException {
        final String token = getTokenFromRequest((HttpServletRequest) request);
        if (token != null && jwtProvider.validateAccessToken(token)) {
            final Claims claims = jwtProvider.getAccessClaims(token);
            final JwtAuthentication jwtInfoToken = JwtUtils.generate(claims);
            jwtInfoToken.setAuthenticated(true);
            SecurityContextHolder.getContext().setAuthentication(jwtInfoToken);
        }
        fc.doFilter(request, response);
    }

    private String getTokenFromRequest(HttpServletRequest request) {
        final String bearer = request.getHeader(AUTHORIZATION);
        if (StringUtils.hasText(bearer) && bearer.startsWith("Bearer ")) {
            return bearer.substring(7);
        }
        return null;
    }

}
