package com.casey.applyflow.controller;

import com.casey.applyflow.repository.EmailTokenRepository;
import com.casey.applyflow.service.AuthService;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import jakarta.servlet.http.HttpServletRequest;

import com.casey.applyflow.domain.EmailVerificationToken;
import com.casey.applyflow.domain.User;
import com.casey.applyflow.dto.LoginRequestDto;
import com.casey.applyflow.dto.RegisterRequestDto;
import com.casey.applyflow.repository.UserRepository;
import com.casey.applyflow.service.CurrentUserProvider;
import com.casey.applyflow.service.TokenService;
import com.casey.applyflow.utils.EmailValidationProvider;

import io.github.bucket4j.Bandwidth;
import io.github.bucket4j.Bucket;

import com.casey.applyflow.dto.UserResponseDto;
import com.casey.applyflow.exception.EmailNotVerifiedException;
import com.casey.applyflow.exception.InvalidEmailException;
import com.casey.applyflow.exception.UserAlreadyExistsException;
import com.casey.applyflow.exception.UserNotFoundException;

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
    private final EmailValidationProvider emailValidationProvider;
    private final Bandwidth loginLimit;
    private final Bandwidth registerLimit;
    private final Map<String, Bucket> loginBuckets = new ConcurrentHashMap<>();
    private final Map<String, Bucket> registerBuckets = new ConcurrentHashMap<>();

    @Value("${jwt.expiration-ms:3600000}")
    private long expirationMs;

    public AuthController(
        AuthenticationManager authenticationManager, 
        TokenService tokenService,
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        CurrentUserProvider currentUserProvider,
        EmailValidationProvider emailValidationProvider, 
        AuthService authService, 
        EmailTokenRepository emailTokenRepository
    ) {
        this.authenticationManager = authenticationManager;
        this.tokenService = tokenService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.currentUserProvider = currentUserProvider;
        this.emailValidationProvider = emailValidationProvider;
        this.authService = authService;
        this.emailTokenRepository = emailTokenRepository;
        
        this.loginLimit = Bandwidth.builder()
            .capacity(5)
            .refillGreedy(2, Duration.ofMinutes(1))
            .build();

        this.registerLimit = Bandwidth.builder()
            .capacity(3)
            .refillGreedy(1, Duration.ofMinutes(1))
            .build();
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

        throw new UserNotFoundException("You must be logged in!");
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
        HttpServletRequest httpRequest,
        @Valid @RequestBody LoginRequestDto request
    ) {
        String clientIp = clientIp(httpRequest);
        Bucket loginBucket = loginBuckets.computeIfAbsent(
            clientIp,
            ip -> Bucket.builder().addLimit(loginLimit).build()
        );

        if (!loginBucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many requests, try again later");
        }

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
        .body("OK");
    }

    @PostMapping("/register")
    public ResponseEntity<String> register(
        HttpServletRequest httpRequest,
        @Valid @RequestBody RegisterRequestDto request
    ) {
        log.warn("REGISTER_START email={}", request.email());

        String clientIp = clientIp(httpRequest);
        Bucket registerBucket = registerBuckets.computeIfAbsent(
            clientIp,
            ip -> Bucket.builder().addLimit(registerLimit).build()
        );

        if (!registerBucket.tryConsume(1)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body("Too many requests, try again later");
        }

        // Check if email already exists
        if (userRepository.findByEmail(request.email()).isPresent()) {
            throw new UserAlreadyExistsException("A user is already signed up with this email");
        }

        // validate email before registration
        if (!emailValidationProvider.validateEmail(request.email())) {
            throw new InvalidEmailException("Failed to register. Please enter a valid email address");
        }

        // Create new user with hashed password
        User user = new User(
                request.name(),
                request.email(),
                passwordEncoder.encode(request.password())  // hash the password
        );

        userRepository.save(user);
        log.warn("REGISTER_USER_SAVED email={} id={}", user.getEmail(), user.getId());

        // handle registration
        authService.register(user);
        log.warn("REGISTER_AUTH_SERVICE_COMPLETED email={}", user.getEmail());

        return ResponseEntity.status(HttpStatus.OK).body("Account created! Please verify you email.");
    }

    @GetMapping("/verify")
    public ResponseEntity<String> verify(@RequestParam String token) {
        // check token exists
        EmailVerificationToken vt = emailTokenRepository.findByToken(token)
            .orElseThrow(() -> new RuntimeException("Invalid token"));

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

        return ResponseEntity.ok("Account verified!");
    }
    

private ResponseCookie accessCookie(String jwt, long maxAgeSeconds) {
    return ResponseCookie.from("ACCESS_TOKEN", jwt)
            .httpOnly(true)
            .secure(false)           // TODO: true in prod -- keep false for local dev.
            .sameSite("None")    
            .path("/api")               
            .maxAge(maxAgeSeconds)
            .build();
}

private String clientIp(HttpServletRequest request) {
    String forwardedFor = request.getHeader("X-Forwarded-For");
    if (forwardedFor != null && !forwardedFor.isBlank()) {
        return forwardedFor.split(",")[0].trim();
    }
    return request.getRemoteAddr();
}

}
