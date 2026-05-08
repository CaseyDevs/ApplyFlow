package com.casey.applyflow.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.casey.applyflow.model.EmailVerificationToken;
import com.casey.applyflow.model.User;
import com.casey.applyflow.repository.EmailTokenRepository;
import com.casey.applyflow.repository.UserRepository;

@Service
public class AuthService {
    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

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

    @Transactional
    public void register(User user) {
        log.warn("AUTH_REGISTER_START email={}", user.getEmail());

        user.setEnabled(false);
        userRepository.save(user);
        log.warn("AUTH_REGISTER_USER_DISABLED_AND_SAVED email={} id={}", user.getEmail(), user.getId());

        // token generation
        String token = UUID.randomUUID().toString(); // TODO: Make token more secure

        // create token
        EmailVerificationToken verificationToken = new EmailVerificationToken();
        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24)); // 24hr token expiry

        // save to db
        emailTokenRepository.save(verificationToken);
        log.warn("AUTH_REGISTER_TOKEN_SAVED email={} token={}...", user.getEmail(), token.substring(0, 8));

        // send email
        emailService.sendVerificationEmail(user, token);
        log.warn("AUTH_REGISTER_SEND_INVOKED email={}", user.getEmail());
    }
}
