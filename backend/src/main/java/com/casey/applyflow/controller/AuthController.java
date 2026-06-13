package com.casey.applyflow.controller;

import com.casey.applyflow.repository.EmailTokenRepository;
import com.casey.applyflow.service.AuthService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import jakarta.servlet.http.HttpServletRequest;

import com.casey.applyflow.dto.LoginRequestDto;
import com.casey.applyflow.dto.RegisterRequestDto;
import com.casey.applyflow.repository.UserRepository;
import com.casey.applyflow.service.CurrentUserProvider;
import com.casey.applyflow.service.RateLimitingService;
import com.casey.applyflow.service.TokenService;
import com.casey.applyflow.utils.ClientIpProvider;
import com.casey.applyflow.dto.UserResponseDto;
import com.casey.applyflow.exception.EmailNotVerifiedException;
import com.casey.applyflow.exception.UserAlreadyExistsException;
import com.casey.applyflow.exception.UserNotFoundException;
import com.casey.applyflow.model.EmailVerificationToken;
import com.casey.applyflow.model.User;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private static final Logger log = LoggerFactory.getLogger(AuthController.class);
    
    private final EmailTokenRepository emailTokenRepository;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final CurrentUserProvider currentUserProvider;
    private final RateLimitingService rateLimitingService;

    @Value("${jwt.expiration-ms:3600000}")
    private long expirationMs;

    @Value("${app.security.cookie-secure:false}")
    private boolean cookieSecure;

    @Value("${app.security.cookie-same-site:Lax}")
    private String cookieSameSite;

    public AuthController(
        AuthenticationManager authenticationManager, 
        TokenService tokenService,
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        CurrentUserProvider currentUserProvider,
        AuthService authService, 
        EmailTokenRepository emailTokenRepository,
        ClientIpProvider clientIpProvider,
        RateLimitingService rateLimitingService
    ) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.currentUserProvider = currentUserProvider;
        this.authService = authService;
        this.emailTokenRepository = emailTokenRepository;
        this.rateLimitingService = rateLimitingService;
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDto> getCurrentUser() {
        User currentUser = currentUserProvider.getCurrentUser();
    
        if (currentUser != null) {
            UserResponseDto response = new UserResponseDto(
                currentUser.getId(),
                currentUser.getName(),
                currentUser.getEmail()
            );

            return ResponseEntity.ok(response);
        }

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
    }
    
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        ResponseCookie clear = ResponseCookie.from("ACCESS_TOKEN", "")
            .httpOnly(true)
            .secure(cookieSecure)
            .sameSite(cookieSameSite)
            .path("/api")
            .maxAge(Duration.ZERO)
            .build();
        
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, clear.toString())
            .body("Logged out");
    }
    

    @PostMapping("/login")
    public ResponseEntity<Void> login(
        HttpServletRequest httpRequest,
        @Valid @RequestBody LoginRequestDto request
    ) {
        rateLimitingService.checkRateLimit(httpRequest, "login", 5, 2, 1);

        User user = userRepository.findByEmail(request.email())
            .orElseThrow(() -> new UserNotFoundException("User not found!"));

        if (!user.isEnabled()) {
            throw new EmailNotVerifiedException("Please verify your email!");
        }

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
        .build();
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
        HttpServletRequest httpRequest,
        @Valid @RequestBody RegisterRequestDto request
    ) {
        log.warn("REGISTER_START email={}", request.email());

        rateLimitingService.checkRateLimit(httpRequest, "register", 3, 1, 5);

        // Check if email already exists
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new UserAlreadyExistsException("A user is already signed up with this email");
        }

        // Create new user with hashed password
        User user = new User(
                request.name(),
                request.email(),
                passwordEncoder.encode(request.password())  // hash the password
        );

        // handle registration (saves user + token in single transaction)
        authService.register(user);
        log.warn("REGISTER_AUTH_SERVICE_COMPLETED email={}", user.getEmail());

        return ResponseEntity.status(HttpStatus.OK).body("Account created! Please verify you email.");
    }

    @PostMapping("/verify")
    @Transactional
    public ResponseEntity<String> verify(@RequestParam String token) {
        // check token exists
        Optional<EmailVerificationToken> optionalToken = emailTokenRepository.findByToken(token);
        if (optionalToken.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid or already used token");
        }
        EmailVerificationToken vt = optionalToken.get();

        // check token has not expired
        if (vt.getExpiryDate().isBefore(LocalDateTime.now())) {
            return ResponseEntity.badRequest().body("Token expired");
        }

        // verify user
        User user = vt.getUser();

        // ensure user is not already verified
        if (user.isEnabled()) {
            return ResponseEntity.badRequest().body("This account is already verified");
        }

        user.setEnabled(true);
        userRepository.save(user);
        emailTokenRepository.delete(vt);

        return ResponseEntity.ok("Account verified!");
    }
    

    private ResponseCookie accessCookie(String jwt, long maxAgeSeconds) {
        return ResponseCookie.from("ACCESS_TOKEN", jwt)
            .httpOnly(true)
            .secure(cookieSecure)
            .sameSite(cookieSameSite)
            .path("/api")               
            .maxAge(maxAgeSeconds)
            .build();
    }

}
