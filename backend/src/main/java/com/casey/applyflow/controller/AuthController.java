package com.casey.applyflow.controller;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import com.casey.applyflow.domain.User;
import com.casey.applyflow.dto.LoginRequestDto;
import com.casey.applyflow.dto.RegisterRequestDto;
import com.casey.applyflow.dto.TokenResponseDto;
import com.casey.applyflow.repository.UserRepository;
import com.casey.applyflow.service.TokenService;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;

    @Value("${jwt.expiration-ms:3600000}")
    private long expirationMs;

    public AuthController(
        AuthenticationManager authenticationManager, 
        TokenService tokenService,
        UserRepository userRepository,
        PasswordEncoder passwordEncoder
    ) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie clear = ResponseCookie.from("ACCESS_TOKEN", "")
            .httpOnly(true)
            .secure(false) // TODO: CHANGE TO TRUE AT PROD
            .sameSite("Lax")
            .path("/api")
            .maxAge(Duration.ZERO)
            .build();
        
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, clear.toString())
            .body("Logged out");
    }
    

    @PostMapping("/login")
    public ResponseEntity<?> login(
        @Valid @RequestBody LoginRequestDto request
    ) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.email(), 
                request.password()
            )
        );

        // Generate token
        String token = tokenService.generateToken(authentication);
        long maxAgeSeconds = expirationMs / 1000;

        // Create cookie with token
        ResponseCookie cookie = accessCookie(token, maxAgeSeconds);

        return ResponseEntity.ok()
        .header(HttpHeaders.SET_COOKIE, cookie.toString())
        .body("OK");
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@Valid @RequestBody RegisterRequestDto request) {
        // Check if email already exists
        if (userRepository.findByEmail(request.email()).isPresent()) {
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body("Email already registered");
        }

        // Create new user with hashed password
        User user = new User(
                request.name(),
                request.email(),
                passwordEncoder.encode(request.password())  // hash the password
        );

        userRepository.save(user);

        return ResponseEntity.status(HttpStatus.CREATED).body("User registered successfully");
    }
    

private ResponseCookie accessCookie(String jwt, long maxAgeSeconds) {
    return ResponseCookie.from("ACCESS_TOKEN", jwt)
            .httpOnly(true)
            .secure(false)           // TODO: true in prod -- keep false for local dev.
            .sameSite("Lax")    
            .path("/api")               
            .maxAge(maxAgeSeconds)
            .build();
}

}
