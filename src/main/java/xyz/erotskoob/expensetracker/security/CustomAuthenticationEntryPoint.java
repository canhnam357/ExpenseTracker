package xyz.erotskoob.expensetracker.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.jspecify.annotations.NonNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import xyz.erotskoob.expensetracker.dto.ErrorDetails;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;


    @Override
    public void commence(@NonNull HttpServletRequest request, HttpServletResponse response,
                         @NonNull AuthenticationException authException) throws IOException {

        HttpStatus unauthorized = HttpStatus.UNAUTHORIZED;
        String detail = request.getRequestURI() + " requires authentication";

        ErrorDetails errorDetails = new ErrorDetails(
                HttpStatus.UNAUTHORIZED,
                detail,
                request.getRequestURI()
        );

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(unauthorized.value());
        response.getWriter().write(objectMapper.writeValueAsString(errorDetails));
    }
}