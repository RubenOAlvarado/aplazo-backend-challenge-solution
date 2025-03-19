package com.bnpl.rubalv.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

public class JwtTokenServiceImplTest {
    private JwtTokenServiceImpl jwtTokenService;

    private final String base64Secret = "MTIzNDU2Nzg5MDEyMzQ1Njc4OTAxMjM0NTY3ODkwMTI=";
    private final long expiration = 86400L;

    @BeforeEach
    void setUp() {
        jwtTokenService = new JwtTokenServiceImpl();
        ReflectionTestUtils.setField(jwtTokenService, "base64Secret", base64Secret);
        ReflectionTestUtils.setField(jwtTokenService, "expiration", expiration);
    }

    @Test
    void testGenerateToken() {
        UUID customerId = UUID.randomUUID();
        String token = jwtTokenService.generateToken(customerId);
        assertThat(token).isNotBlank();
    }

    @Test
    void testValidateToken_ValidToken() {
        UUID customerId = UUID.randomUUID();
        String token = jwtTokenService.generateToken(customerId);

        Authentication auth = jwtTokenService.validateToken(token);
        assertThat(auth).isNotNull();
        assertThat(auth.getPrincipal()).isInstanceOf(User.class);

        User user = (User) auth.getPrincipal();
        assertThat(user.getUsername()).isEqualTo(customerId.toString());
        assertThat(user.getAuthorities()).extracting("authority").contains("ROLE_CUSTOMER");
    }

    @Test
    void testValidateToken_InvalidToken() {
        String invalidToken = "invalid.token.value";
        Authentication auth = jwtTokenService.validateToken(invalidToken);
        assertThat(auth).isNull();
    }

    @Test
    void testValidateToken_ExpiredToken() throws InterruptedException {
        ReflectionTestUtils.setField(jwtTokenService, "expiration", 1L);
        UUID customerId = UUID.randomUUID();
        String token = jwtTokenService.generateToken(customerId);

        Thread.sleep(2000);

        Authentication auth = jwtTokenService.validateToken(token);
        assertThat(auth).isNull();
    }
}
