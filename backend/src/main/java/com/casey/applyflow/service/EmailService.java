package com.casey.applyflow.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import com.casey.applyflow.domain.EmailVerificationToken;
import com.casey.applyflow.domain.User;
import com.casey.applyflow.repository.EmailTokenRepository;

@Service
@EnableAsync
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    
    private final JavaMailSender mailSender;
    private EmailTokenRepository emailTokenRepository;

    @Value("${spring.mail.username}")
    private String mailFrom;

    public EmailService(JavaMailSender mailSender, EmailTokenRepository emailTokenRepository) {
        this.mailSender = mailSender;
        this.emailTokenRepository = emailTokenRepository;
    }

    // send email via java mail sender
    @Async
    public void sendVerificationEmail(User user, String token) {
        log.warn("EMAIL_SEND_START to={}", user.getEmail());
        String link = "http://localhost:8080/api/auth/verify?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(user.getEmail());
        message.setSubject("Verify your account");
        message.setText("Click the link to verify your account: " + link);

        try {
            mailSender.send(message);
            log.warn("EMAIL_SEND_SUCCESS to={}", user.getEmail());
        } catch (Exception ex) {
            log.error("Failed to send verification email to {}", user.getEmail(), ex);
        }
    }

    @Async
    public void sendInvitationEmail(User user, Long jobBoarId) {
        // token generation
        String token = UUID.randomUUID().toString(); // TODO: Make token more secure

        // one-to-one token mapping allows only one token row per user, so update existing token if present
        EmailVerificationToken verificationToken = emailTokenRepository.findByUserId(user.getId())
            .orElseGet(EmailVerificationToken::new);

        verificationToken.setToken(token);
        verificationToken.setUser(user);
        verificationToken.setExpiryDate(LocalDateTime.now().plusHours(24)); // 24hr token expiry

        // save to db
        emailTokenRepository.save(verificationToken);
        log.warn("JOB_BOARD_INVITATION_TOKEN_SAVED email={} token={}...", user.getEmail(), token.substring(0, 8));

        log.warn("INVITATION_EMAIL_SEND_START to={}", user.getEmail());
        String link = "http://localhost:8080/api/v1/job-boards/" + jobBoarId + "/invitation?token=" + token;

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(mailFrom);
        message.setTo(user.getEmail());
        message.setSubject("Job board invitation");
        message.setText("Click the link to join the job board: " + link);

        try {
            mailSender.send(message);
            log.warn("INVITATION_EMAIL_SEND_SUCCESS to={}", user.getEmail());
        } catch (Exception ex) {
            log.error("Failed to send invitation email to {}", user.getEmail(), ex);
        }
    }

}
