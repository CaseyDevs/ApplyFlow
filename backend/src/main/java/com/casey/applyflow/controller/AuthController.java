package com.casey.applyflow.controller;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    @PostMapping("/token")
    public ResponseEntity<TokenResponseDto> login(
        @Valid @RequestBody LoginRequestDto request
    ) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                request.email(), 
                request.password()
            )
        );

        // Authenticate user
        String token = tokenService.generateToken(authentication);

        return ResponseEntity.ok(new TokenResponseDto(token, expirationMs / 1000));
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
    

}
