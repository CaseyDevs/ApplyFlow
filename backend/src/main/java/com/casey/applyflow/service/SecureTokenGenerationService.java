package com.casey.applyflow.service;

import java.security.SecureRandom;
import java.time.Duration;
import java.util.Base64;

import org.springframework.stereotype.Service;

import com.casey.applyflow.interfaces.TokenGenerationStrategy;

@Service
public class SecureTokenGenerationService implements TokenGenerationStrategy {
    private static final SecureRandom secureRandom = new SecureRandom();
    private static final Base64.Encoder base64Encoder = Base64.getUrlEncoder().withoutPadding();
    private static final int TOKEN_LENGTH = 32; // 256 bits

    @Override
    public String generate() {
        byte[] bytes = new byte[TOKEN_LENGTH];
        secureRandom.nextBytes(bytes);
        return base64Encoder.encodeToString(bytes);
    }

    @Override
    public boolean isSecure() {
        return true;
    }

    @Override
    public Duration getExpiration() {
        return Duration.ofHours(24);
    }
}