package com.bnpl.rubalv.service;

import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface JwtTokenService {
    String generateToken(UUID customerId);
    Authentication validateToken(String token);
}
