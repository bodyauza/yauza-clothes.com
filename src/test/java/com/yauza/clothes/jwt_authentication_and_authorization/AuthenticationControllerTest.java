package com.yauza.clothes.jwt_authentication_and_authorization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.yauza.clothes.jwt_authentication_and_authorization.domain.JwtRequest;
import com.yauza.clothes.jwt_authentication_and_authorization.domain.JwtResponse;
import com.yauza.clothes.jwt_authentication_and_authorization.exception.AuthException;
import com.yauza.clothes.jwt_authentication_and_authorization.jwt_service.AuthenticationService;
import com.yauza.clothes.jwt_authentication_and_authorization.jwt_service.JwtProvider;
import io.jsonwebtoken.Claims;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Import;
import org.springframework.http.*;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Base64;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import jakarta.servlet.http.Cookie;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

/**
 * Test class for the {@link AuthenticationController}
 */


@Import(SecurityConfig.class)
@WebMvcTest({AuthenticationController.class})
@AutoConfigureMockMvc(addFilters = false) // Отключаем фильтры безопасности для тестов
class AuthenticationControllerTest {

    private static final String USER_URL = "http://localhost:8081/hello/user";
    private static final String ADMIN_URL = "http://localhost:8081/hello/admin";

    // Внедряем MockMvc для выполнения HTTP-запросов
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AuthenticationService authService;

    @MockBean
    private JwtProvider jwtProvider;

    @MockBean
    private RestTemplateBuilder restTemplateBuilder;

    // ObjectMapper для преобразования объектов в JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    private RestTemplate restTemplateMock;

    // Метод будет вызван перед каждым тестом
    @BeforeEach
    void setUp() {
        restTemplateMock = mock(RestTemplate.class);
        when(restTemplateBuilder.build()).thenReturn(restTemplateMock);
    }

    // Тест успешного входа для пользователя с ролью USER
    @Test
    void login_Success_UserRole() throws Exception {

        JwtRequest request = new JwtRequest();
        request.setLogin("user");
        request.setPassword("password");

        JwtResponse jwtResponse = new JwtResponse("accessToken", "refreshToken");
        when(authService.login(any(JwtRequest.class))).thenReturn(jwtResponse);

        mockJwtProvider(List.of("USER"));

        ResponseEntity<String> mockResponse = ResponseEntity.ok("Hello, User!");
        when(restTemplateMock.exchange(
                eq(USER_URL), // Ожидаемый URL
                eq(HttpMethod.GET),
                any(HttpEntity.class), // Проверяем, что заголовок содержит токен
                eq(String.class)
        )).thenReturn(mockResponse);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request))) // Тело запроса в JSON
                .andExpect(status().isOk()) // Ожидаем статус 200
                .andExpect(jsonPath("$.redirectUrl").value("/hello/user"))
                .andExpect(cookie().exists("refreshToken")) // Проверяем наличие cookie
                .andExpect(cookie().value("refreshToken", "refreshToken")) // Проверяем значение cookie
                .andExpect(cookie().httpOnly("refreshToken", true))
                .andExpect(cookie().path("refreshToken", "/")) // Проверяем путь cookie
                .andExpect(cookie().maxAge("refreshToken", 60 * 60 * 24 * 30));

        verifySecureEndpointCall(USER_URL, jwtResponse.getAccessToken());
    }

    // Тест успешного входа для администратора
    @Test
    void login_Success_AdminRole_WithTokenForwarding() throws Exception {
        JwtRequest request = new JwtRequest();
        request.setLogin("admin");
        request.setPassword("admin");

        JwtResponse jwtResponse = new JwtResponse("accessToken", "refreshToken");
        when(authService.login(any(JwtRequest.class))).thenReturn(jwtResponse);

        mockJwtProvider(List.of("ADMIN"));

        ResponseEntity<String> mockResponse = ResponseEntity.ok("Hello, Admin!");
        when(restTemplateMock.exchange(
                eq(ADMIN_URL),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        )).thenReturn(mockResponse);

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.redirectUrl").value("/hello/admin"));

        verifySecureEndpointCall(ADMIN_URL, jwtResponse.getAccessToken());
    }

    // Тест неудачного входа
    @Test
    void login_Failure_InvalidCredentials() throws Exception {
        JwtRequest request = new JwtRequest();
        request.setLogin("user");
        request.setPassword("wrong");

        when(authService.login(any(JwtRequest.class)))
                .thenThrow(new AuthException("Invalid credentials"));

        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isUnauthorized()) // Ожидаем статус 401
                .andExpect(content().string(containsString("Ошибка входа")));
    }

    @Test
    void login_Success_ButSecureEndpointFails() throws Exception {
        JwtRequest request = new JwtRequest();
        request.setLogin("user");
        request.setPassword("password");

        JwtResponse jwtResponse = new JwtResponse("accessToken", "refreshToken");
        when(authService.login(any(JwtRequest.class))).thenReturn(jwtResponse);

        mockJwtProvider(List.of("USER"));

        // Эмулируем ошибку при вызове защищенного эндпоинта
        when(restTemplateMock.exchange(
                anyString(),
                any(HttpMethod.class),
                any(HttpEntity.class),
                any(Class.class)
        )).thenThrow(new RestClientException("Connection refused"));

        // Ожидаем, что контроллер вернет 200, но выбросит AuthException
        mockMvc.perform(post("/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk()) // /login отработал успешно
                .andExpect(jsonPath("$.redirectUrl").value("/hello/user"));

        // Проверяем, что RestTemplate был вызван
        verify(restTemplateMock).exchange(
                eq(USER_URL),
                eq(HttpMethod.GET),
                any(HttpEntity.class),
                eq(String.class)
        );
    }

    // Тест обновления пары токенов, когда срок действия refreshToken скоро истекает
    @Test
    void getNewAccessToken_RefreshWhenExpiringSoon() throws Exception {
        String refreshToken = "valid.refresh.token";
        JwtResponse jwtResponse = new JwtResponse("newAccess", "newRefresh");

        when(authService.refresh(refreshToken)).thenReturn(jwtResponse);
        when(jwtProvider.getRefreshClaims(refreshToken)).thenReturn(mock(Claims.class));

        // Создаем тестовый токен с истекающим сроком (3 дня)
        String expiredToken = createTestToken(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(3));

        mockMvc.perform(post("/token")
                        .cookie(new Cookie("refreshToken", expiredToken))) // Устанавливаем cookie
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Токен доступа успешно обновлён")))
                .andExpect(cookie().exists("refreshToken")) // Проверяем установку нового cookie
                .andExpect(cookie().value("refreshToken", "newRefresh"));
    }

    // Тест получения access токена без обновления refresh токена
    @Test
    void getNewAccessToken_GetAccessWhenNotExpiringSoon() throws Exception {
        String refreshToken = "valid.refresh.token";
        JwtResponse jwtResponse = new JwtResponse("newAccess", null);

        when(authService.getAccessToken(refreshToken)).thenReturn(jwtResponse);

        // Токен с большим сроком действия (10 дней)
        String validToken = createTestToken(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(10));

        mockMvc.perform(post("/token")
                        .cookie(new Cookie("refreshToken", validToken)))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Токен доступа успешно обновлён")))
                .andExpect(cookie().doesNotExist("refreshToken"));
    }

    /*

    Как работает cookie().doesNotExist("refreshToken") ?
    1. Проверяет, что в ответе нет cookie с именем "refreshToken"
       (т.е. сервер не отправил новый refresh-токен в заголовках `Set-Cookie`);
    2. Если такой cookie есть — тест упадёт с ошибкой.

     */

    // Тест с невалидным токеном
    @Test
    void getNewAccessToken_InvalidToken() throws Exception {
        String invalidToken = "invalid.token";

        when(authService.getAccessToken(invalidToken))
                .thenThrow(new AuthException("Invalid token"));

        mockMvc.perform(post("/token")
                        .cookie(new Cookie("refreshToken", invalidToken)))
                .andExpect(status().isUnauthorized())
                .andExpect(content().string(containsString("Пройдите авторизацию повторно")));
    }

    // Тест без передачи токена
    @Test
    void getNewAccessToken_MissingToken() throws Exception {
        mockMvc.perform(post("/token"))
                .andExpect(status().isBadRequest());
    }

    // Тест выхода из системы
    @Test
    void logout_Success() throws Exception {
        String refreshToken = "valid.refresh.token";
        doNothing().when(authService).logout(refreshToken);

        mockMvc.perform(post("/logout")
                        .cookie(new Cookie("refreshToken", refreshToken)))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/auth"))
                .andExpect(cookie().maxAge("refreshToken", 0)); // Проверяем удаление cookie
    }

    private void mockJwtProvider(List<String> roles) {
        Claims claims = mock(Claims.class);
        when(jwtProvider.getAccessClaims(anyString())).thenReturn(claims);
        when(claims.get("roles", List.class)).thenReturn(roles);
    }

    private void verifySecureEndpointCall(String expectedUrl, String expectedToken) {
        ArgumentCaptor<HttpEntity<?>> captor = ArgumentCaptor.forClass(HttpEntity.class);
        verify(restTemplateMock).exchange(
                eq(expectedUrl), eq(HttpMethod.GET), captor.capture(), eq(String.class)
        );

        HttpHeaders headers = captor.getValue().getHeaders();
        assertEquals("Bearer " + expectedToken,
                headers.getFirst(HttpHeaders.AUTHORIZATION));
    }

    // Вспомогательный метод для создания тестового JWT токена
    private String createTestToken(long expirationTime) {
        // Кодируем header
        String header = Base64.getEncoder().encodeToString("{\"alg\":\"HS256\"}".getBytes());
        // Кодируем payload с expiration time
        String payload = Base64.getEncoder().encodeToString(
                String.format("{\"exp\":%d}", expirationTime / 1000).getBytes());
        return header + "." + payload + ".signature"; // Формируем токен
    }
}
