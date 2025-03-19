package com.bnpl.rubalv.entrypoint;

import com.bnpl.rubalv.exception.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class JwtAuthenticationEntryPointTest {
    @Mock
    private HttpServletRequest request;

    @Mock
    private HttpServletResponse response;

    @Mock
    private AuthenticationException authException;

    @InjectMocks
    private JwtAuthenticationEntryPoint entryPoint;

    @Captor
    private ArgumentCaptor<String> jsonCaptor;

    private final ObjectMapper testMapper = new ObjectMapper();
    private StringWriter responseWriter;

    @BeforeEach
    void setUp() throws IOException {
        responseWriter = new StringWriter();
        when(response.getWriter()).thenReturn(new PrintWriter(responseWriter));
    }

    @Test
    void commence_ShouldSetCorrectResponseStatus() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/secure");

        entryPoint.commence(request, response, authException);

        verify(response).setStatus(HttpStatus.UNAUTHORIZED.value());
    }

    @Test
    void commence_ShouldSetJsonContentType() throws Exception {
        when(request.getRequestURI()).thenReturn("/api/secure");

        entryPoint.commence(request, response, authException);

        verify(response).setContentType(MediaType.APPLICATION_JSON_VALUE);
    }

    @Test
    void commence_ShouldWriteValidErrorResponse() throws Exception {
        String expectedPath = "/api/resource";
        when(request.getRequestURI()).thenReturn(expectedPath);

        entryPoint.commence(request, response, authException);
        String jsonResponse = responseWriter.toString();
        ErrorResponse result = testMapper.readValue(jsonResponse, ErrorResponse.class);

        assertAll(
                () -> assertEquals("APZ000007", result.getCode()),
                () -> assertEquals("UNAUTHORIZED", result.getError()),
                () -> assertEquals("Missing or invalid authentication token", result.getMessage()),
                () -> assertEquals(expectedPath, result.getPath()),
                () -> assertTrue(result.getTimestamp() > 0, "Timestamp should be set")
        );
    }

    @Test
    void commence_ShouldHandleSpecialCharactersInPath() throws Exception {
        String complexPath = "/api/v1/resource?param=value&test=áéíóú";
        when(request.getRequestURI()).thenReturn(complexPath);

        entryPoint.commence(request, response, authException);
        String jsonResponse = responseWriter.toString();
        ErrorResponse result = testMapper.readValue(jsonResponse, ErrorResponse.class);

        assertEquals(complexPath, result.getPath());
    }
}
