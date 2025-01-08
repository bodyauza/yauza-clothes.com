package com.yauza.clothes.jwt_authentication_and_authorization;

import com.yauza.clothes.jwt_authentication_and_authorization.filter.JwtFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.HeaderWriter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http
                .httpBasic().disable()
                .csrf().disable()
                .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/refresh", "/hello/user", "/hello/admin").authenticated()
                        .anyRequest().permitAll()
                        .and()
                        .addFilterAfter(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                )
                .headers(headers -> headers
                        .addHeaderWriter(new CustomHeaderWriter())
                ).build();
    }

    private static class CustomHeaderWriter implements HeaderWriter {
        @Override
        public void writeHeaders(HttpServletRequest request, HttpServletResponse response) {
            // Запрет браузерам выполнять определенные действия с контентом, если тип контента не совпадает
            response.setHeader("X-Content-Type-Options", "nosniff");

            // Включение защиты от XSS
            response.setHeader("X-XSS-Protection", "1; mode=block");

            // Указывает браузерам проверять сертификаты на соответствие перед загрузкой контента
            response.setHeader("Expect-CT", "max-age=30, enforce");

            // Указывает, какие функции могут использоваться на странице
            response.setHeader("Feature-Policy", "geolocation 'self'");

            // Указывает браузеру не кэшировать ответ
            response.setHeader("Cache-Control", "no-store");

//            // Указывает, что браузер должен использовать только безопасные соединения (HTTPS)
//            response.setHeader("Strict-Transport-Security", "max-age=31536000; includeSubDomains");

            // Защищает от внедрения содержимого в iframe
            response.setHeader("X-Frame-Options", "DENY");

            // Указывает политику реферера при переходах по ссылкам
            response.setHeader("Referrer-Policy", "no-referrer-when-downgrade");

            response.setHeader("Content-Security-Policy", "default-src 'self';");

//            response.setHeader("Set-Cookie", "HttpOnly; SameSite=Strict");
        }
    }

}
