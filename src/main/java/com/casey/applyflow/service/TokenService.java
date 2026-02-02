package com.casey.applyflow.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import com.casey.applyflow.domain.User;
import com.casey.applyflow.exception.UserNotFoundException;
import com.casey.applyflow.repository.UserRepository;

@Service
public class TokenService {
    
    private final JwtEncoder jwtEncoder;
    private final UserRepository userRepository;

    @Value("${jwt.expiration-ms:3600000}")
    private long expirationMs;

    public TokenService(JwtEncoder jwtEncoder, UserRepository userRepository) {
        this.jwtEncoder = jwtEncoder;
        this.userRepository = userRepository;
    }

    private Long getUserId(Authentication authentication) {
        String email = authentication.getName();
        User user = userRepository.findByEmail(email)
            .orElseThrow(() -> new UserNotFoundException("User not found with email: " + email));
        return user.getId();
    }

    public String generateToken(Authentication authentication) {
        Instant now = Instant.now();
        Long userId = getUserId(authentication);

        // Token creation
        JwtClaimsSet claims = JwtClaimsSet.builder()
            .issuer("self")
            .issuedAt(now)
            .expiresAt(now.plus(expirationMs, ChronoUnit.MILLIS))
            .subject(authentication.getName())
            .claim("userId", userId)
            .build();

        return jwtEncoder.encode(JwtEncoderParameters.from(claims)).getTokenValue();
    }
}