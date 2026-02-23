package autoservice.web.security;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import autoservice.web.dto.ErrorResponseDto;

/**
 * Ответ 401 при отсутствии/невалидном токене: JSON с сообщением.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        ErrorResponseDto body = new ErrorResponseDto(
            "Не удалось войти в систему. Проверьте логин, пароль или токен.",
            "UNAUTHORIZED",
            HttpServletResponse.SC_UNAUTHORIZED
        );
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }
}
