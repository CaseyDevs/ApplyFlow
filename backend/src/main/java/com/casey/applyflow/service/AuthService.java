package com.casey.applyflow.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;

import com.casey.applyflow.domain.EmailVerificationToken;
import com.casey.applyflow.domain.User;
import com.casey.applyflow.repository.EmailTokenRepository;
import com.casey.applyflow.repository.UserRepository;

import jakarta.validation.constraints.Email;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final EmailTokenRepository emailTokenRepository;
    private final EmailService emailService;

    public AuthService(
        UserRepository userRepository,
        EmailTokenRepository emailTokenRepository,
        EmailService emailService
    ) {
        this.userRepository = userRepository;
        this.emailTokenRepository = emailTokenRepository;
        this.emailService = emailService;
    }

    public void register(User user) {
        user.setEnabled(false);
        userRepository.save(user);

        // token generation
        String token = UUID.randomUUID().toString(); // TODO: Make token more secure

        // create token
        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24)); // 24hr token expiry

        // save to db
        emailTokenRepository.save(verificationToken);

        // send email
        emailService.sendVerificationEmail(user, token);
    }
}
