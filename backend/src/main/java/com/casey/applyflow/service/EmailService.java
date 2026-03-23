package com.casey.applyflow.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Service;

import com.casey.applyflow.domain.User;

@Service
@EnableAsync
public class EmailService {

    private static final Logger log = LoggerFactory.getLogger(EmailService.class);
    
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String mailFrom;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
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

}
